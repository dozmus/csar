package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.util.OptionalUtils;

import java.util.Objects;
import java.util.Optional;

public class ConditionalDescriptor implements Descriptor {

    private final Type controlFlowType;
    private final Optional<String> expression;
    private final Optional<IdentifierName> identifierName;

    public ConditionalDescriptor(Type controlFlowType, Optional<IdentifierName> identifierName,
            Optional<String> expression) {
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

    public Optional<IdentifierName> getIdentifierName() {
        return identifierName;
    }

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionalDescriptor that = (ConditionalDescriptor) o;
        return controlFlowType == that.controlFlowType
                && OptionalUtils.lenientEquals(expression, that.expression)
                && OptionalUtils.lenientEquals(identifierName, that.identifierName);
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
        return new ToStringBuilder(this)
                .append("controlFlowType", controlFlowType)
                .append("expression", expression)
                .append("identifierName", identifierName)
                .toString();
    }

    public enum Type {
        IF, SWITCH, WHILE, DO_WHILE, FOR, FOR_EACH, TERNARY, SYNCHRONIZED
    }
}
