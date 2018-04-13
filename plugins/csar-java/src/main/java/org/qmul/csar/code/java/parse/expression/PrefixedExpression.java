package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;

public class PrefixedExpression implements Expression {

    private final Expression expression;
    private final Prefix prefix;

    public PrefixedExpression(Expression expression, Prefix prefix) {
        this.expression = expression;
        this.prefix = prefix;
    }

    public Expression getExpression() {
        return expression;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixedExpression that = (PrefixedExpression) o;
        return Objects.equals(expression, that.expression) && prefix == that.prefix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, prefix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("expression", expression)
                .append("prefix", prefix)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s%s", StringUtils.indentation(indentation), prefix.getSymbol(),
                expression.toPseudoCode());
    }
}
