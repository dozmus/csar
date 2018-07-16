package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;

import java.util.List;

public interface RefactorChangeFactory<T> {

    /**
     * Returns the refactor changes for the argument target, with respect to the argument generic object.
     * @param t a representation of the new contents
     */
    List<RefactorChange> changes(SerializableCode target, T t);
}
