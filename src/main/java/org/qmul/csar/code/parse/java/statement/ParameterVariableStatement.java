package org.qmul.csar.code.parse.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.ParameterVariableDescriptor;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A constructor, method, or lambda, parameter.
 */
public class ParameterVariableStatement implements Statement {

    private final ParameterVariableDescriptor descriptor;
    private final List<Annotation> annotations;

    public ParameterVariableStatement(ParameterVariableDescriptor descriptor, List<Annotation> annotations) {
        this.descriptor = descriptor;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public ParameterVariableDescriptor getDescriptor() {
        return descriptor;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterVariableStatement that = (ParameterVariableStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, annotations);
    }

    @Override
    public String toString() {
        return String.format("ParameterVariableStatement{descriptor=%s, annotations=%s}", descriptor, annotations);
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();
        annotations.forEach(a -> builder.append(a.toPseudoCode()).append(" "));
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        return builder.append(descriptor.getIdentifierType().get())
                .append(" ")
                .append(descriptor.getIdentifierName().get())
                .toString();
    }
}
