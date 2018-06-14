package org.qmul.csar.code;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.refactor.RefactorTarget;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A project-wide code searcher.
 */
public interface ProjectCodeSearcher {

    /**
     * Returns the results of searching the project code.
     *
     * @return search results
     */
    List<Result> results();

    /**
     * Returns the refactor targets corresponding to the results of searching the project code.
     *
     * @return refactor targets corresponding to search results
     */
    List<RefactorTarget> refactorTargets();

    /**
     * Sets the csar query to search for.
     *
     * @param csarQuery the csar query to search for
     */
    void setCsarQuery(CsarQuery csarQuery);

    /**
     * Sets the project file iterator to search within.
     *
     * @param iterator the project file iterator
     */
    void setIterator(Iterator<Map.Entry<Path, Statement>> iterator);

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
