package org.qmul.csar.lang.descriptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;

import java.util.Objects;

public class AnnotationDescriptor implements Descriptor {

    private final IdentifierName identifierName;
    private final VisibilityModifier visibilityModifier;
    private final boolean abstractModifier;
    private final boolean strictfpModifier;
    private final boolean inner;

    public AnnotationDescriptor(IdentifierName identifierName, VisibilityModifier visibilityModifier,
            boolean abstractModifier, boolean strictfpModifier, boolean inner) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.inner = inner;
    }

    public IdentifierName getIdentifierName() {
        return identifierName;
    }

    public VisibilityModifier getVisibilityModifier() {
        return visibilityModifier;
    }

    public boolean isAbstractModifier() {
        return abstractModifier;
    }

    public boolean isStrictfpModifier() {
        return strictfpModifier;
    }

    public boolean isInner() {
        return inner;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationDescriptor that = (AnnotationDescriptor) o;
        return abstractModifier == that.abstractModifier
                && strictfpModifier == that.strictfpModifier
                && inner == that.inner
                && Objects.equals(identifierName, that.identifierName)
                && visibilityModifier == that.visibilityModifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, visibilityModifier, abstractModifier, strictfpModifier, inner);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("identifierName", identifierName)
                .append("visibilityModifier", visibilityModifier)
                .append("abstractModifier", abstractModifier)
                .append("strictfpModifier", strictfpModifier)
                .append("inner", inner)
                .toString();
    }

    public static class Builder {

        private final IdentifierName identifierName;
        private VisibilityModifier visibilityModifier;
        private boolean abstractModifier;
        private boolean strictfpModifier;
        private boolean inner;

        public Builder(String identifierName) {
            this.identifierName = new IdentifierName.Static(identifierName);
        }

        public Builder(IdentifierName identifierName) {
            this.identifierName = identifierName;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = visibilityModifier;
            return this;
        }

        public Builder abstractModifier(boolean abstractModifier) {
            this.abstractModifier = abstractModifier;
            return this;
        }

        public Builder strictfpModifier(boolean strictfpModifier) {
            this.strictfpModifier = strictfpModifier;
            return this;
        }

        public Builder inner(boolean inner) {
            this.inner = inner;
            return this;
        }

        public AnnotationDescriptor build() {
            return new AnnotationDescriptor(identifierName, visibilityModifier, abstractModifier, strictfpModifier,
                    inner);
        }
    }
}
