package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractVariableDescriptor implements Descriptor {

    private final String identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public AbstractVariableDescriptor(String identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType;
        this.finalModifier = finalModifier;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractVariableDescriptor that = (AbstractVariableDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(identifierType, that.identifierType)
                && Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, identifierType, finalModifier);
    }

    @Override
    public String toString() {
        return String.format("AbstractVariableDescriptor{identifierName='%s', identifierType=%s, finalModifier=%s}",
                identifierName, identifierType, finalModifier);
    }
}