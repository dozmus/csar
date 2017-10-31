package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.parse.java.expression.UnitExpression;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A switch label statement.
 */
public class SwitchLabelStatement implements Statement {

    private final Expression labelExpression;

    public SwitchLabelStatement(String literal) { // Note: 'default' is placed here, with no single quotes, if necessary
        this(new UnitExpression(UnitExpression.ValueType.LITERAL, literal));
    }

    public SwitchLabelStatement(Expression labelExpression) {
        this.labelExpression = labelExpression;
    }

    public Expression getLabelExpression() {
        return labelExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwitchLabelStatement that = (SwitchLabelStatement) o;
        return Objects.equals(labelExpression, that.labelExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelExpression);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("labelExpression", labelExpression)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        String label = labelExpression.toPseudoCode();
        return StringUtils.indentation(indentation) + (label.equals("default") ? "default" : "case " + label) + ":";
    }
}
