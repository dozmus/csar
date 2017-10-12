package org.qmul.csar.code;

import java.nio.file.Path;

public interface PathProcessorErrorListener {

    /**
     * Reports a recoverable parsing error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void reportRecoverableError(Path path, Exception ex);

    /**
     * Reports an unrecoverable, unknown error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void reportUnrecoverableError(Path path, Exception ex);
}
