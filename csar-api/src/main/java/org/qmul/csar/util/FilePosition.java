package org.qmul.csar.util;

import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * A position in a file.
 */
public class FilePosition {

    /**
     * Indexed from 1 to N.
     */
    private int lineNumber;
    /**
     * Indexed from 0 to N.
     */
    private int columnNumber;

    public FilePosition(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public FilePosition(Token token) {
        this(token.getLine(), token.getCharPositionInLine());
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public FilePosition add(int lineNumber, int columnNumber) {
        return new FilePosition(this.lineNumber + lineNumber, this.columnNumber + columnNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePosition that = (FilePosition) o;
        return lineNumber == that.lineNumber && columnNumber == that.columnNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, columnNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("lineNumber", lineNumber)
                .append("columnNumber", columnNumber)
                .toString();
    }
}
