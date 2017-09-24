package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public class ParameterVariableDescriptor implements Descriptor {

    private final Optional<String> identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public ParameterVariableDescriptor(Optional<String> identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType;
        this.finalModifier = finalModifier;
    }

    public ParameterVariableDescriptor(String identifierName, String identifierType, boolean finalModifier) {
        this(Optional.of(identifierName), Optional.of(identifierType), Optional.of(finalModifier));
    }

    public Optional<String> getIdentifierName() {
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
        ParameterVariableDescriptor that = (ParameterVariableDescriptor) o;
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
        return String.format("ParameterVariableDescriptor{identifierName=%s, identifierType=%s, finalModifier=%s}",
                identifierName, identifierType, finalModifier);
    }

    public static class Builder {

        private Optional<String> identifierName = Optional.empty();
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder identifierName(String identifierName) {
            this.identifierName = Optional.of(identifierName);
            return this;
        }

        public Builder identifierType(String identifierType) {
            this.identifierType = Optional.of(identifierType);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public ParameterVariableDescriptor build() {
            return new ParameterVariableDescriptor(identifierName, identifierType, finalModifier);
        }
    }
}
