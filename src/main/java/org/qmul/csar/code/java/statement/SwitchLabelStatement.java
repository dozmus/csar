package org.qmul.csar.code.java.statement;

import org.qmul.csar.code.java.expression.UnitExpression;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.util.Objects;

/**
 * A switch label statement.
 */
public class SwitchLabelStatement implements Statement {

    private Expression labelExpression;

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
        return String.format("SwitchLabelStatement{labelExpression=%s}", labelExpression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return labelExpression.toPseudoCode() + ":";
    }
}
