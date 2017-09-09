package org.qmul.csar.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public final class VirtualProjectCodeIteratorTest {

    private VirtualProjectCodeIterator iterator;

    @Before
    public void setUp() throws Exception {
        iterator = new VirtualProjectCodeIterator();
    }

    @Test
    public void test() throws Exception {
        Assert.assertEquals(false, iterator.hasNext());

        // Add file #1
        Path path1 = Paths.get("file1.txt");
        iterator.addFile(path1);
        Assert.assertEquals(true, iterator.hasNext());
        Assert.assertEquals(path1, iterator.next());
        Assert.assertEquals(false, iterator.hasNext());

        // Try to take non-existent element
        try {
            iterator.next();
        } catch (NoSuchElementException ignored) {
        }

        // Add file #2
        Path path2 = Paths.get("docs/file1.txt");
        iterator.addFile(path2);
        Assert.assertEquals(true, iterator.hasNext());
        Assert.assertEquals(path2, iterator.next());
        Assert.assertEquals(false, iterator.hasNext());

        // Try to take non-existent elements
        try {
            iterator.next();
        } catch (NoSuchElementException ignored) {
        }

        try {
            iterator.next();
        } catch (NoSuchElementException ignored) {
        }
    }
}