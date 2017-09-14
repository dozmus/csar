package org.qmul.csar.lang;

import java.util.Objects;

public class ThrowControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression expression;

    public ThrowControlFlowLanguageElement(Expression expression) {
        super(ControlFlowType.THROW);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ThrowControlFlowLanguageElement that = (ThrowControlFlowLanguageElement) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

    @Override
    public String toString() {
        return String.format("ThrowControlFlowLanguageElement{expression=%s} %s", expression, super.toString());
    }
}
