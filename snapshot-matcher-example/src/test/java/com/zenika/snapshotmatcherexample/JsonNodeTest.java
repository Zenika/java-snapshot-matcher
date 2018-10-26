package com.zenika.snapshotmatcherexample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static com.zenika.snapshotmatcher.SnapshotMatcher.matchesSnapshot;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonNodeTest {
    @Test
    public void testJsonNode () throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree("{\"z\": 1, \"a\": 2 }");
        assertThat(json, matchesSnapshot());
    }
}
