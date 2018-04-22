package org.qmul.csar;

import java.nio.file.Path;

/**
 * A {@link Csar} error listener, this defines the possible errors which may occur throughout the function of Csar.
 * Fatal errors are those from which Csar cannot recover and must terminate.
 */
public interface CsarErrorListener {

    /**
     * Reports a fatal error initializing Csar.
     */
    void fatalErrorInitializing();

    /**
     * Reports a fatal error parsing the csar query.
     *
     * @param ex the exception thrown
     */
    void fatalErrorParsingCsarQuery(Exception ex);

    /**
     * Reports a fatal initializing parsing error.
     *
     * @param ex the exception thrown
     */
    void fatalErrorInitializingParsing(Exception ex);

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
    void fatalErrorPostProcessing(Exception ex);

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
