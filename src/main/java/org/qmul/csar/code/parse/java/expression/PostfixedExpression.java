package org.qmul.csar.code.parse.java.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class PostfixedExpression implements Expression {

    private final Expression expr;
    private final Postfix postfix;

    public PostfixedExpression(Expression expr, Postfix postfix) {
        this.expr = expr;
        this.postfix = postfix;
    }

    public Expression getExpr() {
        return expr;
    }

    public Postfix getPostfix() {
        return postfix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostfixedExpression that = (PostfixedExpression) o;
        return Objects.equals(expr, that.expr) && postfix == that.postfix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, postfix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expr", expr)
                .append("postfix", postfix)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s%s", StringUtils.indentation(indentation), expr.toPseudoCode(), postfix.getSymbol());
    }
}
