package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class CastExpression implements Expression {

    private final String apparentType;
    private final Expression expression;

    public CastExpression(String apparentType, Expression expression) {
        this.apparentType = apparentType;
        this.expression = expression;
    }

    public String getApparentType() {
        return apparentType;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastExpression that = (CastExpression) o;
        return Objects.equals(apparentType, that.apparentType) && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apparentType, expression);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("apparentType", apparentType)
                .append("expression", expression)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + "(" + apparentType + ") " + expression.toPseudoCode();
    }
}
