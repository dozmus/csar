package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.result.Result;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A refactorer, for a specific type of change.
 */
public interface Refactorer {

    List<Result> refactor(Path file, List<RefactorChange> changes) throws IOException;
}
