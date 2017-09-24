package org.qmul.csar.util;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class FilterableIteratorTest {

    private FilterableIterator<Path> it;

    @Before
    public void setUp() throws Exception {
        List<Path> paths = new ArrayList<>();
        paths.add(Paths.get("./Main.java"));
        paths.add(Paths.get("./Super.java"));
        paths.add(Paths.get("./README"));
        paths.add(Paths.get("./"));
        it = new FilterableIterator<>(paths.iterator(), path -> path.getFileName().toString().endsWith(".java"));
    }

    @Test
    public void testUsage() {
        assertTrue(it.hasNext());
        assertEquals(Paths.get("./Main.java"), it.next());
        assertTrue(it.hasNext());
        assertEquals(Paths.get("./Super.java"), it.next());
        assertFalse(it.hasNext());
    }
}