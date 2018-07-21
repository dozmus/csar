package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.refactor.RefactorChange;

import java.util.List;

public interface RefactorChangeFactory<T1, T2> {

    /**
     * Returns the refactor changes for the argument target, with respect to the argument generic object.
     * @param t a representation of the new contents
     */
    List<RefactorChange> changes(T1 target, T2 t);
}
