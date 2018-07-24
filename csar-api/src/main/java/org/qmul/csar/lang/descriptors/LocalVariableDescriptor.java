package org.qmul.csar.lang.descriptors;

import org.qmul.csar.lang.IdentifierName;

import java.util.Optional;

public class LocalVariableDescriptor extends AbstractVariableDescriptor {

    public LocalVariableDescriptor() {
    }

    public LocalVariableDescriptor(IdentifierName identifierName, Optional<String> identifierType,
                                   Optional<Boolean> finalModifier) {
        super(identifierName, identifierType, finalModifier);
    }

    public static class Builder {

        private final IdentifierName identifierName;
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder(String identifierName) {
            this.identifierName = new IdentifierName.Static(identifierName);
        }

        public Builder(IdentifierName identifierName) {
            this.identifierName = identifierName;
        }

        public Builder identifierType(String identifierType) {
            this.identifierType = Optional.of(identifierType);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public LocalVariableDescriptor build() {
            return new LocalVariableDescriptor(identifierName, identifierType, finalModifier);
        }
    }
}
