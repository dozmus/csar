package org.qmul.csar.code.refactor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.ToStringStyles;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A refactor change.
 */
public class RefactorChange {

    private final Path path;
    private final int lineNumber;
    /**
     * The start offset of the change, this is inclusive.
     */
    private final int startOffset;
    /**
     * The end offset of the change, this is exclusive.
     */
    private final int endOffset;
    private final String replacement;

    public RefactorChange(Path path, int lineNumber, int startOffset, int endOffset, String replacement) {
        this.path = path;
        this.lineNumber = lineNumber;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.replacement = replacement;
    }

    public Path getPath() {
        return path;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("path", path)
                .append("lineNumber", lineNumber)
                .append("startOffset", startOffset)
                .append("endOffset", endOffset)
                .append("replacement", replacement)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefactorChange change = (RefactorChange) o;
        return lineNumber == change.lineNumber
                && startOffset == change.startOffset
                && endOffset == change.endOffset
                && Objects.equals(path, change.path)
                && Objects.equals(replacement, change.replacement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, lineNumber, startOffset, endOffset, replacement);
    }
}
