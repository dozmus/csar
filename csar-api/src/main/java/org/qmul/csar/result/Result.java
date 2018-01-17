package org.qmul.csar.result;

import java.nio.file.Path;

/**
 * A match from the searching performed by {@link org.qmul.csar.Csar}.
 */
public final class Result {

    private final Path path;
    private final int lineNumber;
    private final String codeFragment;

    /**
     * Creates a new Result according to the arguments provided.
     *
     * @param path the path which contained this result
     * @param lineNumber the line at which the result was found
     * @param codeFragment the code corresponding to the match
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
}
