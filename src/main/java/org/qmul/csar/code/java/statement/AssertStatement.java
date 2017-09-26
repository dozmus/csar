package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * An assertion statement, it contains the assertion expression and optionally an error message expression.
 */
public class AssertStatement implements Statement {

    private final Expression expression;
    private final Optional<Expression> errorMessageExpression;

    public AssertStatement(Expression expression, Optional<Expression> errorMessageExpression) {
        this.expression = expression;
        this.errorMessageExpression = errorMessageExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Optional<Expression> getErrorMessageExpression() {
        return errorMessageExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssertStatement that = (AssertStatement) o;
        return Objects.equals(expression, that.expression)
                && Objects.equals(errorMessageExpression, that.errorMessageExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, errorMessageExpression);
    }

    @Override
    public String toString() {
        return String.format("AssertStatement{expression=%s, errorMessageExpression=%s}", expression,
                errorMessageExpression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        final String start = StringUtils.indentation(indentation) + "assert " + expression.toPseudoCode();
        return errorMessageExpression.map(expr -> start + " : %s;" + expr.toPseudoCode()).orElseGet(() -> start + ";");
    }
}
