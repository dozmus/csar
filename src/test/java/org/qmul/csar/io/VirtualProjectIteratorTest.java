package org.qmul.csar.io;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public final class VirtualProjectIteratorTest {

    @Test
    public void test() throws Exception {
        VirtualProjectIterator it = new VirtualProjectIterator();
        Assert.assertEquals(false, it.hasNext());

        // Add file #1
        Path path1 = Paths.get("file1.txt");
        it.addFile(path1);
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals(path1, it.next());
        Assert.assertEquals(false, it.hasNext());

        // Try to take non-existent element
        try {
            it.next();
        } catch (NoSuchElementException ignored) {
        }

        // Add file #2
        Path path2 = Paths.get("docs/file1.txt");
        it.addFile(path2);
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals(path2, it.next());
        Assert.assertEquals(false, it.hasNext());

        // Try to take non-existent elements
        try {
            it.next();
        } catch (NoSuchElementException ignored) {
        }

        try {
            it.next();
        } catch (NoSuchElementException ignored) {
        }
    }
}