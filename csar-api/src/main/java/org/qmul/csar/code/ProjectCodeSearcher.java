package org.qmul.csar.code;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

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
     * Returns if an error occurred.
     *
     * @return if an error occurred
     */
    boolean errorOccurred();

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
