package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public class ConditionalDescriptor implements Descriptor {

    private final Type controlFlowType;
    private final Optional<String> expression;
    private final Optional<String> identifierName;

    public ConditionalDescriptor(Type controlFlowType, Optional<String> identifierName, Optional<String> expression) {
        this.controlFlowType = controlFlowType;
        this.identifierName = identifierName;
        this.expression = expression;
    }

    public ConditionalDescriptor(Type controlFlowType) {
        this(controlFlowType, Optional.empty(), Optional.empty());
    }

    public Type getControlFlowType() {
        return controlFlowType;
    }

    public Optional<String> getExpression() {
        return expression;
    }

    public Optional<String> getIdentifierName() {
        return identifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionalDescriptor that = (ConditionalDescriptor) o;
        return controlFlowType == that.controlFlowType
                && Objects.equals(expression, that.expression)
                && Objects.equals(identifierName, that.identifierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlFlowType, expression, identifierName);
    }

    @Override
    public String toString() {
        return String.format("ConditionalDescriptor{controlFlowType=%s, expression=%s, identifierName=%s}",
                controlFlowType, expression, identifierName);
    }

    public enum Type {
        IF, SWITCH, WHILE, DO_WHILE, FOR, FOR_EACH, TERNARY, SYNCHRONIZED
    }
}
