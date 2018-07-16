package org.qmul.csar.code.refactor.writer;

import org.qmul.csar.code.Result;
import org.qmul.csar.code.refactor.RefactorChange;

import java.io.IOException;
import java.util.List;

public interface RefactorChangeWriter {

    /**
     * Writes refactor changes to their corresponding files, and returns their corresponding results.
     * This will not return multiple results for the same line.
     *
     * This will perform the changes in reverse-order, to prevent having to update the indices of subsequent changes.
     *
     * @param changes all of the changes for a specific file
     */
    List<Result> writeAll(List<RefactorChange> changes) throws IOException;
}
