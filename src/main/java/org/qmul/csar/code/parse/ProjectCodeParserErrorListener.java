package org.qmul.csar.code.parse;

import java.nio.file.Path;

public interface ProjectCodeParserErrorListener {

    /**
     * Reports a recoverable parsing error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void reportParsingError(Path path, Exception ex);

    /**
     * Reports an unrecoverable, unknown error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void reportUnknownError(Path path, Exception ex);
}
