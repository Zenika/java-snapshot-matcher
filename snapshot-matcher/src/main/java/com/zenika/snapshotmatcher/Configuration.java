package com.zenika.snapshotmatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class Configuration {


    private static final String ROOT_PACKAGE_PROPERTY_NAME = "rootpackagename";
    private static final String PROPERTIES_FILE_NAME = "snapshotmatcher.properties";

    public static String getRootPackageName() throws MatcherException {
        try {
            return Optional.ofNullable(getProperties().getProperty(ROOT_PACKAGE_PROPERTY_NAME))
                    .orElseThrow(() -> new MatcherException(PROPERTIES_FILE_NAME + " must contain a property " + ROOT_PACKAGE_PROPERTY_NAME));
        } catch (IOException e) {
            throw new MatcherException(PROPERTIES_FILE_NAME + " must exist in resources folder");
        }
    }

    private static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(Files.newBufferedReader(Paths.get("src/test/resources/" + PROPERTIES_FILE_NAME)));
        return prop;
    }
}
