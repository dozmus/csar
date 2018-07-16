package org.qmul.csar.code.refactor.writer;

import java.nio.file.Path;

/**
 * A NOP implementation of {@link RefactorChangeWriter}, this generates results but does not write to files.
 */
public class DummyRefactorChangeWriter extends DefaultRefactorChangeWriter implements RefactorChangeWriter {

    @Override
    public void write(Path path, String contents) {
        // NOP
    }
}
