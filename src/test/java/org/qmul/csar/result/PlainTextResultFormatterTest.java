package org.qmul.csar.result;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class PlainTextResultFormatterTest {

    private ResultFormatter formatter;

    @Before
    public void setUp() throws Exception {
        formatter = new PlainTextResultFormatter();
    }

    private void assertEquals(String expected, Result... results) throws Exception {
        Assert.assertEquals(expected, formatter.format(Arrays.asList(results)));
    }

    @Test
    public void format() throws Exception {
        // Test #1
        Path path1 = Paths.get("test.java");
        int lineNumber1 = 36;
        String codeFragment1 = "new Object();";
        String expected1 = String.format("%s:%s %s", path1.toString(), lineNumber1, codeFragment1);
        Result result1 = new Result(path1, lineNumber1, codeFragment1);
        assertEquals(expected1, result1);

        // Test #2
        Path path2 = Paths.get("org/qmul/Tests.java");
        int lineNumber2 = 1;
        String codeFragment2 = "    for (int i = 0; i < 100; i++)  {";
        String expected2 = String.format("%s:%s %s", path2.toString(), lineNumber2, codeFragment2);
        Result result2 = new Result(path2, lineNumber2, codeFragment2);
        assertEquals(expected1 + System.getProperty("line.separator") + expected2, result1, result2);
    }
}