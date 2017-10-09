package org.qmul.csar.code.parse.java.expression;

import org.qmul.csar.code.parse.java.statement.BlockStatement;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InstantiateClassExpression implements Expression { // TODO allow this to have an Optional<Body>

    private final List<Expression> arguments;
    private final ClassStatement classStatement;
    private final List<String> typeArguments;
    private final boolean hasTypeArguments;

    public InstantiateClassExpression(ClassStatement classStatement, List<Expression> arguments,
            List<String> typeArguments, boolean hasTypeArguments) {
        this.classStatement = classStatement;
        this.arguments = Collections.unmodifiableList(arguments);
        this.typeArguments = Collections.unmodifiableList(typeArguments);
        this.hasTypeArguments = hasTypeArguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public ClassStatement getClassStatement() {
        return classStatement;
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
        return hasTypeArguments == that.hasTypeArguments &&
                Objects.equals(arguments, that.arguments) &&
                Objects.equals(classStatement, that.classStatement) &&
                Objects.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments, classStatement, typeArguments, hasTypeArguments);
    }

    @Override
    public String toString() {
        return String.format("InstantiateClass{classStatement=%s, arguments=%s, typeArguments=%s, "
                        + "hasTypeArguments=%s}", arguments, classStatement, typeArguments, hasTypeArguments);
    }

    @Override
    public String toPseudoCode(int indentation) { // TODO fix this
        StringBuilder builder = new StringBuilder();
        ClassDescriptor descriptor = classStatement.getDescriptor();
        BlockStatement block = classStatement.getBlock();

        if (classStatement.getAnnotations().size() > 0) {
            classStatement.getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(StringUtils.LINE_SEPARATOR));
        }
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

        if (block.equals(BlockStatement.EMPTY)) {
            builder.append(" { }");
        } else {
            builder.append(" {")
                    .append(StringUtils.LINE_SEPARATOR)
                    .append(block.toPseudoCode(indentation + 1))
                    .append(StringUtils.LINE_SEPARATOR)
                    .append(StringUtils.indentation(indentation))
                    .append("}");
        }
        return builder.toString();
    }
}
