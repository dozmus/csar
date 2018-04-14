package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.code.java.parse.statement.MethodStatement;

import java.nio.file.Path;

public class MethodStatementIdentifierRefactorChange implements RefactorChange {

    private final MethodStatement m;

    public MethodStatementIdentifierRefactorChange(MethodStatement m) {
        this.m = m;
    }

    @Override
    public Path path() {
        return m.getPath();
    }

    @Override
    public int lineNumber() {
        return m.getLineNumber();
    }

    @Override
    public int startIndex() {
        return m.getIdentifierStartIdx();
    }

    @Override
    public int endIndex() {
        return m.getIdentifierStartIdx() + m.getDescriptor().getIdentifierName().toString().length();
    }
}
