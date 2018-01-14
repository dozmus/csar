package org.qmul.csar.util;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void indentation() throws Exception {
        assertEquals("", StringUtils.indentation(0));
        assertEquals(StringUtils.INDENTATION_UNIT + StringUtils.INDENTATION_UNIT, StringUtils.indentation(2));
    }

    @Test
    public void getFileNameWithoutExtension() throws Exception {
        assertEquals("file", StringUtils.getFileNameWithoutExtension(Paths.get("file.txt")));
        assertEquals("file", StringUtils.getFileNameWithoutExtension(Paths.get("file")));
    }
}