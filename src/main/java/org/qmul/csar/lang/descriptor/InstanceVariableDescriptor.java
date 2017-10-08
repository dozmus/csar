package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public class InstanceVariableDescriptor extends AbstractVariableDescriptor {

    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> staticModifier;

    public InstanceVariableDescriptor(String identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier, Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> staticModifier) {
        super(identifierName, identifierType, finalModifier);
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Boolean> getStaticModifier() {
        return staticModifier;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceVariableDescriptor that = (InstanceVariableDescriptor) o;
        return Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(staticModifier, that.staticModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibilityModifier, staticModifier);
    }

    @Override
    public String toString() {
        return String.format("InstanceVariableDescriptor{visibilityModifier=%s, staticModifier=%s} %s",
                visibilityModifier, staticModifier, super.toString());
    }

    public static class Builder {

        private final String identifierName;
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();

        public static Builder allFalse(String identifierName) {
            return new Builder(identifierName)
                    .finalModifier(false)
                    .staticModifier(false);
        }

        public Builder(String identifierName) {
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

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public InstanceVariableDescriptor build() {
            return new InstanceVariableDescriptor(identifierName, identifierType, finalModifier, visibilityModifier,
                    staticModifier);
        }
    }
}
