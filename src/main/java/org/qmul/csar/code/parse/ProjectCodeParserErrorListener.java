package org.qmul.csar.code.parse;

import java.nio.file.Path;

public interface ProjectCodeParserErrorListener {

    /**
     * Reports a recoverable parsing error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void parsingError(Path path, Exception ex);

    /**
     * Reports an unrecoverable, unknown error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void unknownError(Path path, Exception ex);
}
