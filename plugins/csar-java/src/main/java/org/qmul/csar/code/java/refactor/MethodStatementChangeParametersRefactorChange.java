package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.code.java.parse.statement.MethodStatement;

import java.nio.file.Path;

public class MethodStatementChangeParametersRefactorChange implements RefactorChange {

    private final MethodStatement statement;

    public MethodStatementChangeParametersRefactorChange(MethodStatement statement) {
        this.statement = statement;
    }

    @Override
    public Path path() {
        return statement.getPath();
    }

    @Override
    public int lineNumber() {
        return statement.getLineNumber();
    }

    /**
     * Returns the start index of the comma (roughly, may need to seek it elsewhere).
     */
    @Override
    public int startIndex() {
        return statement.getIdentifierStartIdx() + statement.getDescriptor().getIdentifierName().toString().length();
    }

    @Override
    public int endIndex() {
        return statement.getIdentifierStartIdx() + statement.getDescriptor().getIdentifierName().toString().length();
    }

    public MethodStatement getStatement() {
        return statement;
    }
}
