package org.qmul.csar.code.refactor;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.code.Result;

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
     * Sets the refactor targets to refactor upon.
     *
     * @param refactorTargets the refactor targets to refactor
     */
    void setRefactorTargets(List<RefactorTarget> refactorTargets);

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
