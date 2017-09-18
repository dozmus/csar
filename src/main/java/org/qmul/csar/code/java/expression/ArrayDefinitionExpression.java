package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayDefinitionExpression implements Expression { // TODO rename class

    private final List<Expression> expr;

    public ArrayDefinitionExpression(List<Expression> expr) {
        this.expr = Collections.unmodifiableList(expr);
    }

    public List<Expression> getExpr() {
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayDefinitionExpression that = (ArrayDefinitionExpression) o;
        return Objects.equals(expr, that.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr);
    }

    @Override
    public String toString() {
        return String.format("ArrayDefinitionExpression{expr=%s}", expr);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation)
                + expr.stream().map(Expression::toPseudoCode).collect(Collectors.joining(" "));
    }
}
