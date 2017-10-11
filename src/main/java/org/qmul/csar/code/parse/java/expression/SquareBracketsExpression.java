package org.qmul.csar.code.parse.java.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class SquareBracketsExpression implements Expression {

    private final Optional<Expression> expression;

    public SquareBracketsExpression(Optional<Expression> expression) {
        this.expression = expression;
    }

    public SquareBracketsExpression() {
        this(Optional.empty());
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
        return new ToStringBuilder(this)
                .append("expression", expression)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation)
                + expression.map(expr -> "[" + expr.toPseudoCode() + "]").orElse("[]");
    }
}
