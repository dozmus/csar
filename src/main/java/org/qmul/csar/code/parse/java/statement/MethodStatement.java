package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A method type declaration.
 */
public class MethodStatement implements Statement {

    private final MethodDescriptor descriptor;
    private final List<ParameterVariableStatement> params;
    private final BlockStatement block;
    private final List<Annotation> annotations;

    public MethodStatement(MethodDescriptor descriptor, List<ParameterVariableStatement> params, BlockStatement block,
            List<Annotation> annotations) {
        this.descriptor = descriptor;
        this.params = Collections.unmodifiableList(params);
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public MethodDescriptor getDescriptor() {
        return descriptor;
    }

    public List<ParameterVariableStatement> getParams() {
        return params;
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
        MethodStatement that = (MethodStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(params, that.params)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, params, block, annotations);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("descriptor", descriptor)
                .append("params", params)
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

        if (descriptor.getVisibilityModifier().isPresent()
                && descriptor.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(descriptor.getVisibilityModifier().get().toPseudoCode()).append(" ");
        }
        StringUtils.append(builder, descriptor.getStaticModifier(), "static ");
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        StringUtils.append(builder, descriptor.getAbstractModifier(), "abstract ");
        StringUtils.append(builder, descriptor.getStrictfpModifier(), "strictfp ");
        StringUtils.append(builder, descriptor.getSynchronizedModifier(), "synchronized ");
        StringUtils.append(builder, descriptor.getNativeModifier(), "native ");
        StringUtils.append(builder, descriptor.getDefaultModifier(), "default ");
        StringUtils.append(builder, descriptor.getOverridden(), "(overridden) ");

        if (descriptor.getTypeParameters().size() > 0) {
            builder.append("<").append(String.join(", ", descriptor.getTypeParameters())).append("> ");
        }
        builder.append(descriptor.getReturnType().map(r -> r + " ").orElse(""))
                .append(descriptor.getIdentifierName());

        if (params.size() > 0) {
            builder.append("(")
                    .append(String.join(", ", params.stream().map(p -> p.toPseudoCode()).collect(Collectors.toList())))
                    .append(")");
        } else {
            builder.append("()");
        }

        if (descriptor.getThrownExceptions().size() > 0) {
            builder.append(" throws ").append(String.join(", ", descriptor.getThrownExceptions())).append("");
        }

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
}
