package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.FilePosition;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodCallExpression implements Expression {

    private final Expression methodName;
    private final List<Expression> arguments;
    private final Path path;
    /**
     * Set during post-processing by {@link MethodCallTypeInstanceResolver}.
     * This is null if unset, or if the source is the same class as the method call is in.
     */
    private TypeInstance methodSource;
    /**
     * Set during post-processing by {@link MethodCallTypeInstanceResolver}.
     */
    private List<TypeInstance> argumentTypes;
    private final FilePosition identifierFilePosition;
    private final FilePosition leftParenthesisPosition;
    private final FilePosition rightParenthesisPosition;
    private final List<FilePosition> commaFilePositions;
    private Optional<List<TypeArgument>> typeArguments;

    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path,
            FilePosition identifierFilePosition, FilePosition leftParenthesisPosition,
            FilePosition rightParenthesisPosition, List<FilePosition> commaFilePositions,
            Optional<List<TypeArgument>> typeArguments) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.path = path;
        this.identifierFilePosition = identifierFilePosition;
        this.leftParenthesisPosition = leftParenthesisPosition;
        this.rightParenthesisPosition = rightParenthesisPosition;
        this.commaFilePositions = commaFilePositions;
        this.typeArguments = typeArguments;
    }

    /**
     * Creates a new MethodCallExpression instance with an empty list for {@link #commaFilePositions}, and with
     * no type arguments.
     */
    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path,
            FilePosition identifierFilePosition, FilePosition leftParenthesisPosition,
            FilePosition rightParenthesisPosition) {
        this(methodName, arguments, path, identifierFilePosition, leftParenthesisPosition, rightParenthesisPosition,
                Collections.emptyList(), Optional.empty());
    }

    /**
     * Creates a new MethodCallExpression instance with no type arguments.
     */
    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path,
            FilePosition identifierFilePosition, FilePosition leftParenthesisPosition,
            FilePosition rightParenthesisPosition, List<FilePosition> commaFilePositions) {
        this(methodName, arguments, path, identifierFilePosition, leftParenthesisPosition, rightParenthesisPosition,
                commaFilePositions, Optional.empty());
    }

    public Expression getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public Path getPath() {
        return path;
    }

    public void setMethodSource(TypeInstance methodSource) {
        this.methodSource = methodSource;
    }

    public void setArgumentTypes(List<TypeInstance> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    public TypeInstance getMethodSource() {
        return methodSource;
    }

    public List<TypeInstance> getArgumentTypes() {
        return argumentTypes;
    }

    public FilePosition getIdentifierFilePosition() {
        return identifierFilePosition;
    }

    public FilePosition getLeftParenthesisPosition() {
        return leftParenthesisPosition;
    }

    public FilePosition getRightParenthesisPosition() {
        return rightParenthesisPosition;
    }

    public List<FilePosition> getCommaFilePositions() {
        return commaFilePositions;
    }

    public String getMethodIdentifier() { // TODO remove this
        return methodIdentifier(methodName);
    }

    public static String methodIdentifier(Expression expr) { // TODO remove this
        return ((UnitExpression) methodIdentifierExpression(expr)).getValue();
    }

    public static Expression methodIdentifierExpression(Expression expr) { // TODO remove this
        if (expr instanceof ParenthesisExpression) { // unwind any parentheses it may be in
            return methodIdentifierExpression(((ParenthesisExpression) expr).getExpression());
        } else if (expr instanceof BinaryExpression) { // java grammar is left-recursive, so we take rhs
            return methodIdentifierExpression(((BinaryExpression) expr).getRight());
        } else if (expr instanceof UnitExpression) {
            return expr;
        } else {
            throw new IllegalArgumentException("invalid expr");
        }
    }

    @Override
    public boolean equals(Object o) { // XXX does not compare argumentTypes or method source
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallExpression that = (MethodCallExpression) o;
        return Objects.equals(methodName, that.methodName)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(path, that.path)
                && Objects.equals(identifierFilePosition, that.identifierFilePosition)
                && Objects.equals(leftParenthesisPosition, that.leftParenthesisPosition)
                && Objects.equals(rightParenthesisPosition, that.rightParenthesisPosition)
                && Objects.equals(commaFilePositions, that.commaFilePositions)
                && Objects.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments, path, identifierFilePosition, leftParenthesisPosition,
                rightParenthesisPosition, commaFilePositions, typeArguments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("methodName", methodName)
                .append("arguments", arguments)
                .append("path", path)
                .append("methodSource", methodSource)
                .append("argumentTypes", argumentTypes)
                .append("identifierFilePosition", identifierFilePosition)
                .append("leftParenthesisPosition", leftParenthesisPosition)
                .append("rightParenthesisPosition", rightParenthesisPosition)
                .append("commaFilePositions", commaFilePositions)
                .append("typeArguments", typeArguments)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) { // TODO include type arguments
        StringBuilder sb = new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append(methodName.toPseudoCode()).append("(");

        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toPseudoCode());

            if (i + 1 < arguments.size())
                sb.append(", ");
        }
        return sb.append(")").toString();
    }
}
