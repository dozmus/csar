package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.util.OptionalUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

public class InstanceVariableDescriptor extends AbstractVariableDescriptor {

    private Optional<VisibilityModifier> visibilityModifier;
    private Optional<Boolean> staticModifier;

    public InstanceVariableDescriptor() {
    }

    public InstanceVariableDescriptor(IdentifierName identifierName, Optional<String> identifierType,
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
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InstanceVariableDescriptor that = (InstanceVariableDescriptor) o;
        return OptionalUtils.lenientEquals(visibilityModifier, that.visibilityModifier)
                && OptionalUtils.lenientEquals(staticModifier, that.staticModifier);
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
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("visibilityModifier", visibilityModifier)
                .append("staticModifier", staticModifier)
                .toString();
    }

    public static class Builder {

        private final IdentifierName identifierName;
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
