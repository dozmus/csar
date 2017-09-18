package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.descriptor.InstanceVariableDescriptor;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.VisibilityModifier;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstanceVariableStatement implements Statement {

    private final InstanceVariableDescriptor descriptor;
    private final List<Annotation> annotations;
    private final Optional<Expression> valueExpression;

    public InstanceVariableStatement(InstanceVariableDescriptor descriptor, List<Annotation> annotations,
            Optional<Expression> valueExpression) {
        this.descriptor = descriptor;
        this.annotations = Collections.unmodifiableList(annotations);
        this.valueExpression = valueExpression;
    }

    public InstanceVariableDescriptor getDescriptor() {
        return descriptor;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public Optional<Expression> getValueExpression() {
        return valueExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceVariableStatement that = (InstanceVariableStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(annotations, that.annotations)
                && Objects.equals(valueExpression, that.valueExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, annotations, valueExpression);
    }

    @Override
    public String toString() {
        return String.format("InstanceVariableStatement{descriptor=%s, annotations=%s, valueExpression=%s}", descriptor,
                annotations, valueExpression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(StringUtils.LINE_SEPARATOR));
        }
        builder.append(StringUtils.indentation(indentation));

        if (descriptor.getVisibilityModifier().isPresent()
                && descriptor.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(descriptor.getVisibilityModifier().get().toString().toLowerCase()).append(" ");
        }
        StringUtils.append(builder, descriptor.getStaticModifier(), "static ");
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        return builder.append(descriptor.getIdentifierType().map(t -> t + " ").orElse(""))
                .append(descriptor.getIdentifierName())
                .append(valueExpression.map(expression -> " = " + expression.toPseudoCode()).orElse(""))
                .append(";")
                .toString();
    }
}
