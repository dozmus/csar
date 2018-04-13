package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;

public class PostfixedExpression implements Expression {

    private final Expression expression;
    private final Postfix postfix;

    public PostfixedExpression(Expression expression, Postfix postfix) {
        this.expression = expression;
        this.postfix = postfix;
    }

    public Expression getExpression() {
        return expression;
    }

    public Postfix getPostfix() {
        return postfix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostfixedExpression that = (PostfixedExpression) o;
        return Objects.equals(expression, that.expression) && postfix == that.postfix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, postfix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("expression", expression)
                .append("postfix", postfix)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s%s", StringUtils.indentation(indentation), expression.toPseudoCode(),
                postfix.getSymbol());
    }
}
