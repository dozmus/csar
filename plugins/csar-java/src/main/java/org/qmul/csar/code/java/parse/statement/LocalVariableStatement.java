package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.LocalVariableDescriptor;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A local variable statement.
 */
public class LocalVariableStatement implements Statement {

    private final LocalVariableDescriptor descriptor;
    private final Optional<Expression> valueExpression;
    private final List<Annotation> annotations;

    public LocalVariableStatement(LocalVariableDescriptor descriptor, Optional<Expression> valueExpression,
            List<Annotation> annotations) {
        this.descriptor = descriptor;
        this.valueExpression = valueExpression;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public LocalVariableDescriptor getDescriptor() {
        return descriptor;
    }

    public Optional<Expression> getValueExpression() {
        return valueExpression;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalVariableStatement that = (LocalVariableStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(valueExpression, that.valueExpression)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, valueExpression, annotations);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("descriptor", descriptor)
                .append("valueExpression", valueExpression)
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
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        return builder.append(descriptor.getIdentifierType().map(t -> t + " ").orElse(""))
                .append(descriptor.getIdentifierName())
                .append(valueExpression.map(expression -> " = " + expression.toPseudoCode()).orElse(""))
                .append(";")
                .toString();
    }
}
