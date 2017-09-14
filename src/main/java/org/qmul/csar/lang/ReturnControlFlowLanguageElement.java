package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class ReturnControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Optional<Expression> expression;

    public ReturnControlFlowLanguageElement(Optional<Expression> expression) {
        super(ControlFlowType.RETURN);
        this.expression = expression;
    }

    public Optional<Expression> getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReturnControlFlowLanguageElement that = (ReturnControlFlowLanguageElement) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

    @Override
    public String toString() {
        return String.format("ReturnControlFlowLanguageElement{expression=%s} %s", expression, super.toString());
    }
}
