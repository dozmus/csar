package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayInitializationExpression implements Expression {

    private final List<Expression> expr;

    public ArrayInitializationExpression(List<Expression> expr) {
        this.expr = Collections.unmodifiableList(expr);
    }

    public List<Expression> getExpr() {
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayInitializationExpression that = (ArrayInitializationExpression) o;
        return Objects.equals(expr, that.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr);
    }

    @Override
    public String toString() {
        return String.format("ArrayInitializationExpression{expr=%s}", expr);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation)
                + expr.stream().map(Expression::toPseudoCode).collect(Collectors.joining(" "));
    }
}
