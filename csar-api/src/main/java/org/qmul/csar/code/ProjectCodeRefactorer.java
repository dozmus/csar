package org.qmul.csar.code;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.result.Result;

import java.util.List;

/**
 * A project-wide code refactorer.
 */
public interface ProjectCodeRefactorer {

    /**
     * Returns the results of refactoring the project code.
     *
     * @return refactor results
     */
    List<Result> results();

    /**
     * Sets the refactor descriptor to carry out.
     *
     * @param descriptor the refactor to apply
     */
    void setRefactorDescriptor(RefactorDescriptor descriptor);

    /**
     * Sets the search results to refactor upon.
     *
     * @param searchResults the search results to refactor
     */
    void setSearchResults(List<RefactorTarget> searchResults);

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
