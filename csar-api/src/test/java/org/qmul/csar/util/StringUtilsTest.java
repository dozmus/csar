package org.qmul.csar.util;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testIndentationEmptyString() {
        assertEquals("", StringUtils.indentation(0));
    }

    @Test
    public void testIndentation() {
        assertEquals(StringUtils.INDENTATION_UNIT + StringUtils.INDENTATION_UNIT, StringUtils.indentation(2));
    }

    @Test
    public void testFileNameWithoutExtension() {
        assertEquals("file", StringUtils.fileNameWithoutExtension(Paths.get("file.txt")));
        assertEquals("file", StringUtils.fileNameWithoutExtension(Paths.get("file")));
    }

    @Test
    public void testCount() {
        assertEquals(1, StringUtils.count("hellomyfriendohellao", "hello"));
        assertEquals(0, StringUtils.count("hellomyfriendohellao", "Hello"));
        assertEquals(0, StringUtils.count("helomyfriendohellao", "hello"));
    }

    @Test
    public void testCountEmptyString() {
        assertEquals(0, StringUtils.count("Hello World!", ""));
        assertEquals(0, StringUtils.count("", ""));
    }
}