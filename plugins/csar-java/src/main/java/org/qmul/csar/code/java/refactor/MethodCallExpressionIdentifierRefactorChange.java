package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;

import java.nio.file.Path;

public class MethodCallExpressionIdentifierRefactorChange implements RefactorChange {

    private final MethodCallExpression e;

    public MethodCallExpressionIdentifierRefactorChange(MethodCallExpression e) {
        this.e = e;
    }

    @Override
    public Path path() {
        return e.getPath();
    }

    @Override
    public int lineNumber() {
        return e.getLineNumber();
    }

    @Override
    public int startIndex() {
        return e.getLeftParenthesisPosition().getColumnNumber() - e.getMethodIdentifier().length();
    }

    @Override
    public int endIndex() {
        return e.getLeftParenthesisPosition().getColumnNumber();
    }
}
