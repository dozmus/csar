package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class AssertControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression expression;
    private final Optional<Expression> errorMessage;

    public AssertControlFlowLanguageElement(Expression expression, Optional<Expression> errorMessage) {
        super(ControlFlowType.ASSERT);
        this.expression = expression;
        this.errorMessage = errorMessage;
    }

    public Expression getExpression() {
        return expression;
    }

    public Optional<Expression> getAssertionErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssertControlFlowLanguageElement that = (AssertControlFlowLanguageElement) o;
        return Objects.equals(expression, that.expression) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, errorMessage);
    }

    @Override
    public String toString() {
        return String.format("AssertControlFlowLanguageElement{expression=%s, assertionErrorMessage=%s}", expression,
                errorMessage);
    }
}
