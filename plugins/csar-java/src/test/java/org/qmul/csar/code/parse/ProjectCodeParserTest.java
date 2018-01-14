package org.qmul.csar.code.parse;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

public final class ProjectCodeParserTest {

    private static final Iterator<Path> VALID_ITERATOR = Arrays.asList(Paths.get(".")).iterator();

    @Test(expected = IllegalArgumentException.class)
    public void createWithInvalidThreadNumber1() {
        new ProjectCodeParser(null, VALID_ITERATOR, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithInvalidThreadNumber2() {
        new ProjectCodeParser(null, VALID_ITERATOR, -5);
    }

    @Test(expected = NullPointerException.class)
    public void createWithInvalidIterator() {
        new ProjectCodeParser(null, null);
    }
}
