package com.zenika.snapshotmatcherexample;

import static com.zenika.snapshotmatcher.SnapshotMatcher.matchesSnapshot;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class SimpleTypesTest {

    @Test
    public void testString() {
        String s = "Mystring";

        assertThat(s, matchesSnapshot());
    }

    @Test
    public void testNumber() {
        assertThat(17711L, matchesSnapshot());
    }

    @Test
    public void testBoolean() {
        assertThat(false, matchesSnapshot());
    }
}
