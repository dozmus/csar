package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.descriptors.AnnotationDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

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
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("descriptor", descriptor)
                .append("block", block)
                .append("annotations", annotations)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(System.lineSeparator()));
        }
        builder.append(StringUtils.indentation(indentation));
        builder.append(descriptor.getVisibilityModifier().get().toPseudoCode()).append(" ");

        if (descriptor.getAbstractModifier().orElse(false)) {
            builder.append("abstract ");
        }

        if (descriptor.getStrictfpModifier().orElse(false)) {
            builder.append("strictfp ");
        }

        if (descriptor.getInner().orElse(false)) {
            builder.append("(inner) ");
        }
        builder.append(descriptor.getIdentifierName());

        if (block.equals(BlockStatement.EMPTY)) {
            builder.append(" { }");
        } else {
            builder.append(" {")
                    .append(System.lineSeparator())
                    .append(block.toPseudoCode(indentation + 1))
                    .append(System.lineSeparator())
                    .append(StringUtils.indentation(indentation))
                    .append("}");
        }
        return builder.toString();
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
            return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                    .append("visibilityModifier", visibilityModifier)
                    .append("abstractModifier", abstractModifier)
                    .append("identifierName", identifierName)
                    .append("defaultValue", defaultValue)
                    .append("annotations", annotations)
                    .toString();
        }

        @Override
        public String toPseudoCode(int indentation) {
            StringBuilder builder = new StringBuilder();

            if (getAnnotations().size() > 0) {
                getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                        .append(System.lineSeparator()));
            }
            builder.append(StringUtils.indentation(indentation));
            builder.append(visibilityModifier.toPseudoCode()).append(" ");

            if (abstractModifier) {
                builder.append("abstract ");
            }
            builder.append(identifierName).append("()");
            defaultValue.ifPresent(value -> builder.append(" default ").append(value.toPseudoCode())); // TODO fix this
            return builder.append(";").toString();
        }
    }
}
