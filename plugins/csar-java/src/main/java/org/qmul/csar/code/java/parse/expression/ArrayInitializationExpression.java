package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayInitializationExpression implements Expression {

    private final List<Expression> expressions;

    public ArrayInitializationExpression(List<Expression> expressions) {
        this.expressions = Collections.unmodifiableList(expressions);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayInitializationExpression that = (ArrayInitializationExpression) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expressions", expressions)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation)
                + expressions.stream().map(Expression::toPseudoCode).collect(Collectors.joining(" "));
    }
}
