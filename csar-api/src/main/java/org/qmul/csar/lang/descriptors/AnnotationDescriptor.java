package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.util.OptionalUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

public class AnnotationDescriptor implements Descriptor {

    private final IdentifierName identifierName;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> abstractModifier;
    private final Optional<Boolean> strictfpModifier;
    private final Optional<Boolean> inner;

    public AnnotationDescriptor(IdentifierName identifierName, Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier, Optional<Boolean> inner) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.inner = inner;
    }

    public IdentifierName getIdentifierName() {
        return identifierName;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
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

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationDescriptor that = (AnnotationDescriptor) o;
        return identifierName.nameEquals(that.identifierName)
                && OptionalUtils.lenientEquals(visibilityModifier, that.visibilityModifier)
                && OptionalUtils.lenientEquals(abstractModifier, that.abstractModifier)
                && OptionalUtils.lenientEquals(strictfpModifier, that.strictfpModifier)
                && OptionalUtils.lenientEquals(inner, that.inner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationDescriptor that = (AnnotationDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(abstractModifier, that.abstractModifier)
                && Objects.equals(strictfpModifier, that.strictfpModifier)
                && Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, visibilityModifier, abstractModifier, strictfpModifier, inner);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_MULTI_LINE_STYLE)
                .append("identifierName", identifierName)
                .append("visibilityModifier", visibilityModifier)
                .append("abstractModifier", abstractModifier)
                .append("strictfpModifier", strictfpModifier)
                .append("inner", inner)
                .toString();
    }

    public static class Builder {

        private final IdentifierName identifierName;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();

        public static Builder allFalse(String identifierName) {
            return new Builder(identifierName)
                    .abstractModifier(false)
                    .strictfpModifier(false)
                    .inner(false);
        }

        public Builder(String identifierName) {
            this.identifierName = new IdentifierName.Static(identifierName);
        }

        public Builder(IdentifierName identifierName) {
            this.identifierName = identifierName;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
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

        public AnnotationDescriptor build() {
            return new AnnotationDescriptor(identifierName, visibilityModifier, abstractModifier, strictfpModifier,
                    inner);
        }
    }
}
