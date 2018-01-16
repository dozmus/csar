package org.qmul.csar;

import org.pf4j.ExtensionPoint;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.util.List;

/**
 * A Csar implementation for a specific language.
 */
public interface CsarPlugin extends ExtensionPoint {

    /**
     * Returns if parsing the project code was successful.
     *
     * @param projectDirectory the project directory
     * @param narrowSearch if the search domain should be narrowed
     * @param ignoreFile the csar ignore file to use
     * @param threadCount the amount of threads to use
     * @return is parsing successful
     */
    boolean parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount);

    /**
     * Returns if post-processing the project code was successful.
     *
     * @return is post-processing successful
     */
    boolean postprocess();

    /**
     * Returns the results of searching the project code.
     *
     * @param csarQuery the csar query to search for
     * @param threadCount the amount of threads to use
     * @return search results
     * @throws Exception if an unrecoverable error occurs
     */
    List<Result> search(CsarQuery csarQuery, int threadCount) throws Exception;
}
