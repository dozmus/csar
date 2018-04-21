package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;

import java.nio.file.Path;

public class MethodCallExpressionChangeParametersRefactorChange implements RefactorChange {

    private final MethodCallExpression expression;

    public MethodCallExpressionChangeParametersRefactorChange(MethodCallExpression expression) {
        this.expression = expression;
    }

    @Override
    public Path path() {
        return expression.getPath();
    }

    @Override
    public int lineNumber() {
        return expression.getLineNumber();
    }

    @Override
    public int startIndex() {
        return expression.getLeftParenthesisPosition().getColumnNumber() - expression.getMethodIdentifier().length();
    }

    @Override
    public int endIndex() {
        return expression.getLeftParenthesisPosition().getColumnNumber();
    }

    public MethodCallExpression getExpression() {
        return expression;
    }
}
