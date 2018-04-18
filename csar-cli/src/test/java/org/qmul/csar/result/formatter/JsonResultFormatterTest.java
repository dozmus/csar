package org.qmul.csar.result.formatter;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class JsonResultFormatterTest {

    private final ResultFormatter formatter = new JsonResultFormatter();

    private void assertEquals(String expected, Result... results) throws Exception {
        Assert.assertEquals(expected, formatter.format(Arrays.asList(results)));
    }

    @Test
    public void testValidSingleResultFormat() throws Exception {
        // Expected
        Path path = Paths.get("test.java");
        int lineNumber = 36;
        String codeFragment = "new Object();";
        String expected = "[ {" + System.lineSeparator()
                + "  \"path\" : \"" + path.toUri().toString() + "\"," + System.lineSeparator()
                + "  \"lineNumber\" : 36," + System.lineSeparator()
                + "  \"codeFragment\" : \"new Object();\"" + System.lineSeparator()
                + "} ]";

        // Assert
        Result result = new Result(path, lineNumber, codeFragment);
        assertEquals(expected, result);
    }

    @Test
    public void testValidTwoResultsFormat() throws Exception {
        // Expected
        Path path1 = Paths.get("test.java");
        int lineNumber1 = 36;
        String codeFragment1 = "new Object();";

        Path path2 = Paths.get("org/qmul/Tests.java");
        int lineNumber2 = 1;
        String codeFragment2 = "    for (int i = 0; i < 100; i++)  {";
        String expected = "[ {" + System.lineSeparator()
                + "  \"path\" : \"" + path1.toUri().toString() + "\"," + System.lineSeparator()
                + "  \"lineNumber\" : 36," + System.lineSeparator()
                + "  \"codeFragment\" : \"new Object();\"" + System.lineSeparator()
                + "}, {" + System.lineSeparator()
                + "  \"path\" : \"" + path2.toUri().toString() + "\"," + System.lineSeparator()
                + "  \"lineNumber\" : 1," + System.lineSeparator()
                + "  \"codeFragment\" : \"    for (int i = 0; i < 100; i++)  {\"" + System.lineSeparator()
                + "} ]";

        // Assert
        Result result1 = new Result(path1, lineNumber1, codeFragment1);
        Result result2 = new Result(path2, lineNumber2, codeFragment2);
        assertEquals(expected, result1, result2);
    }

    @Test
    public void testEmptyResultsList() throws Exception {
        assertEquals("[ ]");
    }
}
