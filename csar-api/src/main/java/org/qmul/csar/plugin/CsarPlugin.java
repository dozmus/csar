package org.qmul.csar.plugin;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.util.List;

/**
 * A Csar implementation for a specific language.
 */
public interface CsarPlugin {

    /**
     * Parses the project code.
     *
     * @param projectDirectory the project directory
     * @param narrowSearch if the search domain should be narrowed
     * @param ignoreFile the csar ignore file to use
     * @param threadCount the amount of threads to use
     */
    void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount);

    /**
     * Post-processes the code.
     *
     * @param threadCount the amount of threads to use
     */
    void postprocess(int threadCount);

    /**
     * Returns the results of searching the project code.
     *
     * @param csarQuery the csar query to search for
     * @param threadCount the amount of threads to use
     * @return search results
     */
    List<Result> search(CsarQuery csarQuery, int threadCount);

    /**
     * Returns the results of refactoring the project code.
     *
     * @param csarQuery the csar query to search for
     * @param searchResults the search results
     * @return search results
     */
    List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults);

    /**
     * Adds an error listener.
     *
     * @param errorListener the error listener
     */
    void addErrorListener(CsarErrorListener errorListener);

    /**
     * Removes an error listener.
     *
     * @param errorListener the error listener
     */
    void removeErrorListener(CsarErrorListener errorListener);
}
