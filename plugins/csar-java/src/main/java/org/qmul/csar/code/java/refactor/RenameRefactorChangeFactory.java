package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RenameRefactorChangeFactory implements RefactorChangeFactory<Renamable, String> {

    public List<RefactorChange> changes(Renamable target, String newName) {
        return Collections.singletonList(change(target, newName));
    }

    public RefactorChange change(Renamable target, String newName) {
        Path path = target.getPath();
        String identifier = target.getIdentifierName();
        FilePosition identifierPos = target.getIdentifierFilePosition();
        int endOffset = identifierPos.getFileOffset() + identifier.length();
        return new RefactorChange(path, identifierPos.getLineNumber(), identifierPos.getFileOffset(), endOffset,
                newName);
    }
}
