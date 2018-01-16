package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class PrefixedExpression implements Expression {

    private final Expression expr;
    private final Prefix prefix;

    public PrefixedExpression(Expression expr, Prefix prefix) {
        this.expr = expr;
        this.prefix = prefix;
    }

    public Expression getExpr() {
        return expr;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedExpression that = (PrefixedExpression) o;
        return Objects.equals(expr, that.expr) && prefix == that.prefix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, prefix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expr", expr)
                .append("prefix", prefix)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s%s", StringUtils.indentation(indentation), prefix.getSymbol(), expr.toPseudoCode());
    }
}
