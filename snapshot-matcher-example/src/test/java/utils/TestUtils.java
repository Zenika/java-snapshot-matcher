package utils;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String fileName, Class<T> clazz) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(String.format("data/%s/%s.json", clazz.getSimpleName(), fileName));

        try {
            return objectMapper.readValue(resource, clazz);
        } catch (IOException e) {
            fail("File not found:\n" + e.getLocalizedMessage());
            return null;
        }
    }
}
