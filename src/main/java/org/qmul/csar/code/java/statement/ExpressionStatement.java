package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A semi-colon terminated expression.
 */
public class ExpressionStatement implements Statement {

    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionStatement statement = (ExpressionStatement) o;
        return Objects.equals(expression, statement.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return String.format("ExpressionStatement{expression=%s}", expression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + expression.toPseudoCode() + ";";
    }
}
