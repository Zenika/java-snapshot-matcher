package com.zenika.snapshotmatcher;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import difflib.DiffUtils;
import difflib.Patch;

public class SnapshotMatcher<T> extends TypeSafeMatcher<T> {
    /**
     * Factory method to instantiate a snapshot matcher with the given type
     *
     * @param <T> Type of object to snapshot
     * @return The snapshot matcher instance
     */
    @Factory
    public static <T> SnapshotMatcher<T> matchesSnapshot() {
        return new SnapshotMatcher<>();
    }

    /**
     * Private constructor, use factory method {@link SnapshotMatcher#matchesSnapshot()} create a new matcher
     */
    private SnapshotMatcher() {
    }

    private final DeterministicObjectMapper objectMapper = new DeterministicObjectMapper();

    @Override
    public boolean matchesSafely(T o) {
        Path snapshotPath = getPath();

        if (Files.exists(snapshotPath)) {
            // File exists => Compare snapshot file to given object
            return compareSnapshot(o, snapshotPath);
        } else {
            // File doesn't exist => Create snapshot file and return true
            createSnapshot(o, snapshotPath);
            return true;
        }
    }

    /**
     * Perform serialization of o and save result to snapshotPath
     *
     * @param o            Object to serialize
     * @param snapshotPath Path to file to create
     */
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

    /**
     * Compares snapshot at the given path with the given object
     *
     * @param o            Actual object to serialize then compare
     * @param snapshotPath Path to the corresponding snapshot file
     * @return true if snapshot matches the actual, false otherwise
     */
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
        description.appendText("Object should match snapshot at " + getPath().toString());
    }

    /**
     * Find out path of the snapshot using caller name and class
     *
     * @return Path to the snapshot file
     */
    private Path getPath() {
        StackTraceElement caller = getCaller();

        String callerClassName = caller.getClassName().replace('.', '/');
        String callerMethodName = caller.getMethodName();

        return Paths.get(String.format("src/test/resources/snapshots/%s/%s.json", callerClassName, callerMethodName));
    }

    /**
     * Find out caller StackTraceElement, ie. the test method which instantiated the matcher
     *
     * @return StackTraceElement of the test method
     */
    private StackTraceElement getCaller() {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        return Stream.of(stackTraceElements)
                // Filter out java.lang package
                .filter(stackTraceElement -> !stackTraceElement.getClassName().startsWith(Thread.class.getPackage().getName()))
                // Filter out org.hamcrest package
                .filter(stackTraceElement -> !stackTraceElement.getClassName().startsWith(TypeSafeMatcher.class.getPackage().getName()))
                // Filter out current class
                .filter(stackTraceElement -> !stackTraceElement.getClassName().equals(SnapshotMatcher.class.getName()))
                .findFirst()
                .orElse(null);
    }

}
