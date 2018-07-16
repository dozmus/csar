package org.qmul.csar.code.search;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.Result;
import org.qmul.csar.lang.SerializableCode;
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
     */
    List<Result> results();

    /**
     * Returns the objects corresponding to the results of searching the project code.
     */
    List<SerializableCode> resultObjects();

    /**
     * Sets the csar query to search for.
     */
    void setCsarQuery(CsarQuery csarQuery);

    /**
     * Sets the project file iterator to search within.
     */
    void setIterator(Iterator<Map.Entry<Path, Statement>> iterator);

    /**
     * Adds an error listener.
     */
    void addErrorListener(CsarErrorListener errorListener);

    /**
     * Removes an error listener.
     */
    void removeErrorListener(CsarErrorListener errorListener);
}
