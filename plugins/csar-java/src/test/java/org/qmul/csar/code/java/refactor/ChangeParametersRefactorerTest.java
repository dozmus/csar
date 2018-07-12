package org.qmul.csar.code.java.refactor;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.code.java.refactor.refactorer.ChangeParametersRefactorer;
import org.qmul.csar.util.FilePosition;

import java.util.ArrayList;
import java.util.List;

public class ChangeParametersRefactorerTest {

    @Test
    public void testSetLinesSingleLineCollapse() {
        // Prepare
        List<String> lines = new ArrayList<>();
        lines.add("this is a line");
        lines.add("");
        lines.add("this is another line!");

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("this is a line");
        expectedLines.add("hellois another line!");

        // Assert
        assertEquals(expectedLines, lines, new FilePosition(2, 0), new FilePosition(3, 5), "hello", false);
    }

    @Test
    public void testSetLinesSingleLineBlankLines() {
        // Prepare
        List<String> lines = new ArrayList<>();
        lines.add("this is a line");
        lines.add("");
        lines.add("this is another line!");

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("this is a line");
        expectedLines.add("hellois another line!");
        expectedLines.add("");

        // Assert
        assertEquals(expectedLines, lines, new FilePosition(2, 0), new FilePosition(3, 5), "hello", true);
    }

    @Test
    public void testSetLinesSingleLine() {
        // Prepare
        List<String> lines = new ArrayList<>();
        lines.add("this is a line");
        lines.add("this is another line!");

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("hellois a line");
        expectedLines.add("this is another line!");

        // Assert
        assertEquals(expectedLines, lines, new FilePosition(1, 0), new FilePosition(1, 5), "hello", false);
    }

    @Test
    public void testSetLinesMultiLine() {
        // Prepare
        List<String> lines = new ArrayList<>();
        lines.add("this is a line");
        lines.add("this is another line!");

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("this is a his is another line!");

        // Assert
        assertEquals(expectedLines, lines, new FilePosition(1, 10), new FilePosition(2, 1), "", false);
    }

    private void assertEquals(List<String> expectedLines, List<String> lines, FilePosition startPosition,
            FilePosition endPosition, String content, boolean blankLines) {
        ChangeParametersRefactorer.setLines(lines, startPosition, endPosition, content, blankLines);
        Assert.assertEquals(expectedLines, lines);
    }
}
