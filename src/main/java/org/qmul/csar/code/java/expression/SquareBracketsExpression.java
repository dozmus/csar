package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class SquareBracketsExpression implements Expression { // [] or [$expr]

    private final Optional<Expression> expression;

    public SquareBracketsExpression(Optional<Expression> expression) {
        this.expression = expression;
    }

    public Optional<Expression> getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SquareBracketsExpression that = (SquareBracketsExpression) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return String.format("SquareBracketsExpression{expression=%s}", expression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation)
                + expression.map(expr -> "[" + expr.toPseudoCode() + "]").orElse("[]");
    }
}
