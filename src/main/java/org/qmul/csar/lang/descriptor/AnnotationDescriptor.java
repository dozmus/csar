package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

public class AnnotationDescriptor implements Descriptor {

    private final String identifierName;
    private final VisibilityModifier visibilityModifier;
    private final boolean abstractModifier;
    private final boolean strictfpModifier;
    private final boolean inner;

    public AnnotationDescriptor(String identifierName, VisibilityModifier visibilityModifier, boolean abstractModifier,
            boolean strictfpModifier, boolean inner) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.inner = inner;
    }

    public String getIdentifierName() {
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
    public String toString() {
        return String.format("AnnotationDescriptor{identifierName='%s', visibilityModifier=%s, abstractModifier=%s, "
                        + "strictfpModifier=%s, inner=%s}",
                identifierName, visibilityModifier, abstractModifier, strictfpModifier, inner);
    }

    public static class Builder {

        private final String identifierName;
        private VisibilityModifier visibilityModifier;
        private boolean abstractModifier;
        private boolean strictfpModifier;
        private boolean inner;

        public Builder(String identifierName) {
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
