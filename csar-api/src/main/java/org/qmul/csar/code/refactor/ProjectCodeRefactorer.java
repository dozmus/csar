package org.qmul.csar.code.refactor;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.code.Result;

import java.util.List;

/**
 * A project-wide code refactorer.
 */
public interface ProjectCodeRefactorer {

    /**
     * Returns the results of refactoring the project code.
     */
    List<Result> results();

    /**
     * Sets the refactor descriptor to carry out.
     */
    void setRefactorDescriptor(RefactorDescriptor descriptor);

    /**
     * Sets the refactor targets to refactor upon.
     */
    void setSearchResultObjects(List<SerializableCode> searchResultObjects);

    /**
     * Adds an error listener.
     */
    void addErrorListener(CsarErrorListener errorListener);

    /**
     * Removes an error listener.
     */
    void removeErrorListener(CsarErrorListener errorListener);
}
