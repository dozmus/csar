package org.qmul.csar;

import java.nio.file.Path;

public interface CsarErrorListener {

    /**
     * Reports a fatal error initializing Csar.
     */
    void errorInitializing();

    /**
     * Reports a fatal error parsing the csar query.
     *
     * @param ex the exception thrown
     */
    void errorParsingCsarQuery(Exception ex);

    /**
     * Reports a fatal initializing parsing error.
     *
     * @param ex the exception thrown
     */
    void fatalInitializingParsing(Exception ex);

    /**
     * Reports a recoverable parsing error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void errorParsing(Path path, Exception ex);

    /**
     * Reports a fatal parsing error.
     *
     * @param path the file which was being parsed when the error occurred
     * @param ex the exception thrown
     */
    void fatalErrorParsing(Path path, Exception ex);

    /**
     * Reports a fatal error post-processing the code.
     *
     * @param ex the exception thrown
     */
    void errorPostProcessing(Exception ex);

    /**
     * Reports a recoverable searching error.
     *
     * @param path the file which was being searched when the error occurred
     * @param ex the exception thrown
     */
    void errorSearching(Path path, Exception ex);

    /**
     * Reports a fatal searching error.
     *
     * @param path the file which was being searched when the error occurred
     * @param ex the exception thrown
     */
    void fatalErrorSearching(Path path, Exception ex);

    /**
     * Reports a recoverable refactoring error.
     *
     * @param path the file which was being refactored when the error occurred
     * @param ex the exception thrown
     */
    void errorRefactoring(Path path, Exception ex);

    /**
     * Reports a fatal refactoring error.
     *
     * @param path the file which was being refactored when the error occurred
     * @param ex the exception thrown
     */
    void fatalErrorRefactoring(Path path, Exception ex);
}
