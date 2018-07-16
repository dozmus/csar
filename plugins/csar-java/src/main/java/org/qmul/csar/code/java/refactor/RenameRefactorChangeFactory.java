package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RenameRefactorChangeFactory implements RefactorChangeFactory<String> {

    public List<RefactorChange> changes(SerializableCode target, String newName) {
        return Collections.singletonList(change(target, newName));
    }

    public RefactorChange change(SerializableCode target, String newName) {
        Path path;
        FilePosition identifierPos;
        String identifier;

        if (target instanceof MethodStatement) {
            MethodStatement ms = (MethodStatement) target;
            path = ms.getPath();
            identifier = ms.getDescriptor().getIdentifierName().toString();
            identifierPos = ms.getIdentifierFilePosition();
        } else if (target instanceof MethodCallExpression) {
            MethodCallExpression ms = (MethodCallExpression) target;
            path = ms.getPath();
            identifier = ms.getMethodIdentifier();
            identifierPos = ms.getIdentifierFilePosition();
        } else {
            throw new IllegalArgumentException("invalid target");
        }

        int endOffset = identifierPos.getFileOffset() + identifier.length();
        return new RefactorChange(path, identifierPos.getLineNumber(), identifierPos.getFileOffset(), endOffset,
                newName);
    }
}
