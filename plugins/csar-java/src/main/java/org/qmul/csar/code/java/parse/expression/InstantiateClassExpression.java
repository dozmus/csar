package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.java.parse.statement.BlockStatement;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.descriptors.ClassDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstantiateClassExpression implements Expression {

    private final ClassDescriptor descriptor;
    private final Optional<BlockStatement> block;
    private final List<Expression> arguments;
    private final List<String> typeArguments;
    private final boolean hasTypeArguments;

    public InstantiateClassExpression(ClassDescriptor descriptor, Optional<BlockStatement> block,
            List<Expression> arguments, List<String> typeArguments, boolean hasTypeArguments) {
        this.descriptor = descriptor;
        this.block = block;
        this.arguments = Collections.unmodifiableList(arguments);
        this.typeArguments = Collections.unmodifiableList(typeArguments);
        this.hasTypeArguments = hasTypeArguments;
    }

    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    public Optional<BlockStatement> getBlock() {
        return block;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public List<String> getTypeArguments() {
        return typeArguments;
    }

    public boolean hasTypeArguments() {
        return hasTypeArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstantiateClassExpression that = (InstantiateClassExpression) o;
        return hasTypeArguments == that.hasTypeArguments
                && Objects.equals(descriptor, that.descriptor)
                && Objects.equals(block, that.block)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, block, arguments, typeArguments, hasTypeArguments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("descriptor", descriptor)
                .append("block", block)
                .append("arguments", arguments)
                .append("typeArguments", typeArguments)
                .append("hasTypeArguments", hasTypeArguments)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) { // TODO fix this
        StringBuilder builder = new StringBuilder();

        builder.append(StringUtils.indentation(indentation));
        builder.append("new ");

        if (descriptor.getVisibilityModifier().isPresent()
                && descriptor.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(descriptor.getVisibilityModifier().get().toPseudoCode()).append(" ");
        }

        StringUtils.append(builder, descriptor.getStaticModifier(), "static ");
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        StringUtils.append(builder, descriptor.getAbstractModifier(), "abstract ");
        StringUtils.append(builder, descriptor.getStrictfpModifier(), "strictfp ");

        builder.append(descriptor.getIdentifierName());

        if (hasTypeArguments && typeArguments.size() > 0) {
            builder.append("<").append(String.join(", ", typeArguments)).append(">");
        }

        if (arguments.size() > 0) {
            String args = "(";

            for (int i = 0; i < arguments.size(); i++) {
                args += arguments.get(i).toPseudoCode();

                if (i + 1 < arguments.size())
                    args += ", ";
            }
            builder.append(args).append(")");
        } else {
            builder.append("()");
        }
        block.ifPresent(blockStatement -> builder.append(" {")
                .append(System.lineSeparator())
                .append(blockStatement.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}"));
        return builder.toString();
    }
}
