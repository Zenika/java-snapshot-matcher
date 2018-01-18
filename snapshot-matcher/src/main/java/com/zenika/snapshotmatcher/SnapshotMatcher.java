package com.zenika.snapshotmatcher;

import static com.zenika.snapshotmatcher.Configuration.getRootPackageName;
import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import difflib.DiffUtils;
import difflib.Patch;

public class SnapshotMatcher<T> extends TypeSafeMatcher<T> {
    @Factory
    public static <T> SnapshotMatcher<T> matchesSnapshot() {
        return new SnapshotMatcher<>();
    }

    private SnapshotMatcher() {
    }

    private final DeterministicObjectMapper objectMapper = new DeterministicObjectMapper();

    @Override
    public boolean matchesSafely(T o) {
        Path snapshotPath;
        try {
            snapshotPath = getPath();
        } catch (MatcherException e) {
            return false;
        }

        if (Files.exists(snapshotPath)) {
            // File exists => Compare snapshot file to given object
            return compareSnapshot(o, snapshotPath);
        } else {
            // File doesn't exist => Create snapshot file and return true
            createSnapshot(o, snapshotPath);
            return true;
        }
    }

    private void createSnapshot(T o, Path snapshotPath) {
        try {
            Files.createDirectories(snapshotPath.getParent());
            Files.createFile(snapshotPath);

            try (BufferedWriter writer = Files.newBufferedWriter(snapshotPath, Charset.forName("UTF-8"))) {
                objectMapper.writeValue(writer, o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean compareSnapshot(T o, Path snapshotPath) {
        try (BufferedReader reader = Files.newBufferedReader(snapshotPath, Charset.forName("UTF-8"))) {
            List<String> actual = asList(objectMapper.writeValueAsString(o).split(System.lineSeparator()));
            List<String> expected = new ArrayList<>(actual.size());

            String line;
            while ((line = reader.readLine()) != null) {
                expected.add(line);
            }

            Patch<String> patch = DiffUtils.diff(actual, expected);

            if (patch.getDeltas().isEmpty()) {
                return true;
            } else {
                System.out.print(
                        patch.getDeltas().stream()
                                .map(delta -> String.format("Expected\t<%s>\nbut found\t<%s>", delta.getOriginal(), delta.getRevised()))
                                .collect(
                                        Collectors.joining(
                                                System.lineSeparator() + System.lineSeparator(),
                                                String.format("Snapshot mismatch (%d differences found):\n", patch.getDeltas().size()),
                                                System.lineSeparator())));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        try {
            description.appendText("Object should match snapshot at " + getPath().toString());
        } catch (MatcherException e) {
            e.printStackTrace();
        }
    }

    private Path getPath() throws MatcherException {
        StackTraceElement caller = getCaller();

        String callerClassName = caller.getClassName();
        String callerMethodName = caller.getMethodName();

        return Paths.get(String.format("src/test/resources/snapshots/%s/%s.json", callerClassName, callerMethodName));
    }

    private StackTraceElement getCaller() throws MatcherException {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        final String rootPackageName = getRootPackageName();

        return Stream.of(stackTraceElements)
                .filter(stackTraceElement ->
                        stackTraceElement.getClassName().startsWith(rootPackageName)
                                && !stackTraceElement.getClassName().equals(getClass().getName()))
                .findFirst()
                .orElseThrow(MatcherException::new);
    }

}
