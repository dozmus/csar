package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.descriptor.AnnotationDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An annotation type declaration.
 */
public class AnnotationStatement implements TypeStatement {

    private final AnnotationDescriptor descriptor;
    private final BlockStatement block;
    private final List<Annotation> annotations;

    public AnnotationStatement(AnnotationDescriptor descriptor, BlockStatement block, List<Annotation> annotations) {
        this.descriptor = descriptor;
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public AnnotationDescriptor getDescriptor() {
        return descriptor;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationStatement that = (AnnotationStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, block, annotations);
    }

    @Override
    public String toString() {
        return String.format("AnnotationStatement{descriptor=%s, block=%s, annotations=%s}", descriptor, block,
                annotations);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "annotation_def"; // TODO write
    }

    public static class AnnotationMethod implements Statement {

        private final VisibilityModifier visibilityModifier;
        private final boolean abstractModifier;
        private final String identifierName;
        private final Optional<Annotation.Value> defaultValue;
        private final List<Annotation> annotations;

        public AnnotationMethod(VisibilityModifier visibilityModifier, boolean abstractModifier,
                String identifierName, Optional<Annotation.Value> defaultValue, List<Annotation> annotations) {
            this.visibilityModifier = visibilityModifier;
            this.abstractModifier = abstractModifier;
            this.identifierName = identifierName;
            this.defaultValue = defaultValue;
            this.annotations = Collections.unmodifiableList(annotations);
        }

        public VisibilityModifier getVisibilityModifier() {
            return visibilityModifier;
        }

        public boolean isAbstractModifier() {
            return abstractModifier;
        }

        public String getIdentifierName() {
            return identifierName;
        }

        public Optional<Annotation.Value> getDefaultValue() {
            return defaultValue;
        }

        public List<Annotation> getAnnotations() {
            return annotations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnnotationMethod that = (AnnotationMethod) o;
            return abstractModifier == that.abstractModifier
                    && visibilityModifier == that.visibilityModifier
                    && Objects.equals(identifierName, that.identifierName)
                    && Objects.equals(defaultValue, that.defaultValue)
                    && Objects.equals(annotations, that.annotations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(visibilityModifier, abstractModifier, identifierName, defaultValue, annotations);
        }

        @Override
        public String toString() {
            return String.format("AnnotationMethod{visibilityModifier=%s, abstractModifier=%s, identifierName='%s', "
                            + "defaultValue=%s, annotations=%s}",
                    visibilityModifier, abstractModifier, identifierName, defaultValue, annotations);
        }

        @Override
        public String toPseudoCode(int indentation) {
            return "annotation_method"; // TODO write
        }
    }
}
