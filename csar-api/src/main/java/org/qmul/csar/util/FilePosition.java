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
    private int fileOffset;

    public FilePosition() {
    }

    public FilePosition(int lineNumber, int fileOffset) {
        this.lineNumber = lineNumber;
        this.fileOffset = fileOffset;
    }

    public FilePosition(Token token) {
        this(token.getLine(), token.getStartIndex());
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getFileOffset() {
        return fileOffset;
    }

    public FilePosition add(int offset) {
        return new FilePosition(this.lineNumber, this.fileOffset + offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePosition that = (FilePosition) o;
        return lineNumber == that.lineNumber && fileOffset == that.fileOffset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, fileOffset);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("lineNumber", lineNumber)
                .append("fileOffset", fileOffset)
                .toString();
    }
}
