package org.qmul.csar.lang.descriptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.qmul.csar.lang.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EnumDescriptor implements Descriptor {

    private final String identifierName;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> staticModifier;
    private final Optional<Boolean> finalModifier;
    private final Optional<Boolean> abstractModifier;
    private final Optional<Boolean> strictfpModifier;
    private final Optional<Boolean> inner;
    private final List<String> superClasses;

    public EnumDescriptor(String identifierName, Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> staticModifier, Optional<Boolean> finalModifier, Optional<Boolean> abstractModifier,
            Optional<Boolean> strictfpModifier, Optional<Boolean> inner, List<String> superClasses) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.inner = inner;
        this.superClasses = superClasses;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Boolean> getStaticModifier() {
        return staticModifier;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    public Optional<Boolean> getAbstractModifier() {
        return abstractModifier;
    }

    public Optional<Boolean> getStrictfpModifier() {
        return strictfpModifier;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumDescriptor that = (EnumDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(staticModifier, that.staticModifier)
                && Objects.equals(finalModifier, that.finalModifier)
                && Objects.equals(abstractModifier, that.abstractModifier)
                && Objects.equals(strictfpModifier, that.strictfpModifier)
                && Objects.equals(inner, that.inner)
                && Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, visibilityModifier, staticModifier, finalModifier, abstractModifier,
                strictfpModifier, inner, superClasses);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("identifierName", identifierName)
                .append("visibilityModifier", visibilityModifier)
                .append("staticModifier", staticModifier)
                .append("finalModifier", finalModifier)
                .append("abstractModifier", abstractModifier)
                .append("strictfpModifier", strictfpModifier)
                .append("inner", inner)
                .append("superClasses", superClasses)
                .toString();
    }

    public static class Builder {

        private String identifierName;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();
        private List<String> superClasses = new ArrayList<>();

        public static Builder allFalse(String identifierName) {
            return new Builder(identifierName)
                    .staticModifier(false)
                    .finalModifier(false)
                    .abstractModifier(false)
                    .strictfpModifier(false)
                    .inner(false);
        }

        public Builder(String identifierName) {
            this.identifierName = identifierName;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public Builder abstractModifier(boolean abstractModifier) {
            this.abstractModifier = Optional.of(abstractModifier);
            return this;
        }

        public Builder strictfpModifier(boolean strictfpModifier) {
            this.strictfpModifier = Optional.of(strictfpModifier);
            return this;
        }

        public Builder inner(boolean inner) {
            this.inner = Optional.of(inner);
            return this;
        }

        public Builder superClasses(List<String> superClasses) {
            this.superClasses = superClasses;
            return this;
        }

        public EnumDescriptor build() {
            return new EnumDescriptor(identifierName, visibilityModifier, staticModifier, finalModifier,
                    abstractModifier, strictfpModifier, inner, superClasses);
        }
    }
}
