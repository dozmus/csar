package org.qmul.csar.output;

import java.nio.file.Path;

public final class Result {

    private final Path path;
    private final int lineNumber;
    private final String codeFragment;

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
