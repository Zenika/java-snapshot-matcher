package com.zenika.snapshotmatcher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.Writer;

/**
 * Deterministic {@link ObjectMapper} to avoid unpredictable serialization
 *
 * - Sorts properties and map entries by keys
 *
 * - Uses pretty printer by default to have readable diffs
 *
 * - Ignore null fields to reduce snapshot size
 *
 * <strong> Be aware that we don't sort collections, so avoid using
 * {@link java.util.Set} implementations that have unpredictable iteration
 * order, e.g {@link java.util.HashSet} </strong>
 */
class DeterministicObjectMapper {

    private final ObjectMapper objectMapper;

    DeterministicObjectMapper() {
        objectMapper = JsonMapper
            .builder()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .build();
    }

    public void writeValue(Writer writer, Object o) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, getSnapshotableObject(o));
    }

    public <T> String writeValueAsString(T o) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getSnapshotableObject(o));
    }

    public Object getSnapshotableObject (Object o) throws JsonProcessingException {
        if (o instanceof JsonNode) return objectMapper.treeToValue((TreeNode) o, Object.class);
        return o;
    }
}

