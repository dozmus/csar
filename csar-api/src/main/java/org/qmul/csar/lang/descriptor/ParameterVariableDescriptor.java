package org.qmul.csar.lang.descriptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;

import java.util.Objects;
import java.util.Optional;

public class ParameterVariableDescriptor implements Descriptor {

    private final Optional<IdentifierName> identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public ParameterVariableDescriptor(Optional<IdentifierName> identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType; // NOTE can end with '...' (varargs) or '<...>' (generic typing)
        this.finalModifier = finalModifier;
    }

    public ParameterVariableDescriptor(IdentifierName identifierName, String identifierType, boolean finalModifier) {
        this(Optional.of(identifierName), Optional.of(identifierType), Optional.of(finalModifier));
    }

    public Optional<IdentifierName> getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
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
        return new ToStringBuilder(this)
                .append("identifierName", identifierName)
                .append("identifierType", identifierType)
                .append("finalModifier", finalModifier)
                .toString();
    }

    public static class Builder {

        private Optional<IdentifierName> identifierName = Optional.empty();
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder identifierName(String identifierName) {
            this.identifierName = Optional.of(new IdentifierName.Static(identifierName));
            return this;
        }

        public Builder identifierName(IdentifierName identifierName) {
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
