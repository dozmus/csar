package org.qmul.csar.result;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.io.PathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class JsonResultFormatterTest {

    private final ResultFormatter formatter = new JsonResultFormatter();

    private void assertEquals(String expected, Result... results) throws Exception {
        Assert.assertEquals(expected, formatter.format(Arrays.asList(results)));
    }

    @Test
    public void testValidOutputFormat() throws Exception {
        // Test #1
        Path path1 = Paths.get("test.java");
        int lineNumber1 = 36;
        String codeFragment1 = "new Object();";
        String expected1 = PathHelper.read(Paths.get("src/test/resources/org/qmul/csar/result/Expected1.json"));
        Result result1 = new Result(path1, lineNumber1, codeFragment1);
        assertEquals(expected1, result1);

        // Test #2
        Path path2 = Paths.get("org/qmul/Tests.java");
        int lineNumber2 = 1;
        String codeFragment2 = "    for (int i = 0; i < 100; i++)  {";
        String expected2 = PathHelper.read(Paths.get("src/test/resources/org/qmul/csar/result/Expected2.json"));
        Result result2 = new Result(path2, lineNumber2, codeFragment2);
        assertEquals(expected2, result1, result2);
    }

    @Test
    public void testEmptyResultsList() throws Exception {
        assertEquals("[ ]");
    }
}
