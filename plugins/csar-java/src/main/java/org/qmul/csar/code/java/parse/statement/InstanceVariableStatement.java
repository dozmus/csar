package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.InstanceVariableDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An instance variable statement.
 */
public class InstanceVariableStatement implements Statement {

    private InstanceVariableDescriptor descriptor;
    private List<Annotation> annotations;
    private Optional<Expression> valueExpression;

    public InstanceVariableStatement() {
    }

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
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("descriptor", descriptor)
                .append("annotations", annotations)
                .append("valueExpression", valueExpression)
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

        if (descriptor.getVisibilityModifier().isPresent()
                && descriptor.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(descriptor.getVisibilityModifier().get().toPseudoCode()).append(" ");
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
