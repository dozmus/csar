package org.qmul.csar.result;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.ToStringStyles;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A match from the searching performed by {@link org.qmul.csar.Csar}.
 */
public class Result {

    private final Path path;
    private final int lineNumber;
    private final String codeFragment;

    /**
     * Creates a new Result according to the arguments provided.
     *
     * @param path the path which contained this result
     * @param lineNumber the line at which the result was found
     * @param codeFragment the result's code fragment
     */
    public Result(Path path, int lineNumber, String codeFragment) {
        this.path = path;
        this.lineNumber = lineNumber;
        this.codeFragment = codeFragment;
    }

    public Path getPath() {
        return path;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCodeFragment() {
        return codeFragment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return lineNumber == result.lineNumber
                && Objects.equals(path, result.path)
                && Objects.equals(codeFragment, result.codeFragment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, lineNumber, codeFragment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("path", path)
                .append("lineNumber", lineNumber)
                .append("codeFragment", codeFragment)
                .toString();
    }
}
