package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;

public class TernaryExpression implements Expression {

    private Expression condition;
    private Expression valueIfTrue;
    private Expression valueIfFalse;

    public TernaryExpression() {
    }

    public TernaryExpression(Expression condition, Expression valueIfTrue, Expression valueIfFalse) {
        this.condition = condition;
        this.valueIfTrue = valueIfTrue;
        this.valueIfFalse = valueIfFalse;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getValueIfTrue() {
        return valueIfTrue;
    }

    public Expression getValueIfFalse() {
        return valueIfFalse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TernaryExpression that = (TernaryExpression) o;
        return Objects.equals(condition, that.condition)
                && Objects.equals(valueIfTrue, that.valueIfTrue)
                && Objects.equals(valueIfFalse, that.valueIfFalse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, valueIfTrue, valueIfFalse);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("condition", condition)
                .append("valueIfTrue", valueIfTrue)
                .append("valueIfFalse", valueIfFalse)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s ? %s : %s", StringUtils.indentation(indentation), condition.toPseudoCode(),
                valueIfTrue.toPseudoCode(), valueIfFalse.toPseudoCode());
    }
}
