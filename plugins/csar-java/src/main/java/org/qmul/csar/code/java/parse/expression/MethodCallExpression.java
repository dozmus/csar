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

public class MethodCallExpression implements Expression {

    private final Expression methodName;
    private final List<Expression> arguments;
    private final Path path;
    private final int lineNumber;
    /**
     * Set during post-processing by {@link MethodCallTypeInstanceResolver}.
     * This is null if unset, or if the source is the same class as the method call is in.
     */
    private TypeInstance methodSource;
    /**
     * Set during post-processing by {@link MethodCallTypeInstanceResolver}.
     */
    private List<TypeInstance> argumentTypes;
    private final FilePosition leftParenthesisPosition;
    private final FilePosition rightParenthesisPosition;
    private final List<FilePosition> commaFilePositions;

    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path, int lineNumber,
            FilePosition leftParenthesisPosition, FilePosition rightParenthesisPosition,
            List<FilePosition> commaFilePositions) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.path = path;
        this.lineNumber = lineNumber;
        this.leftParenthesisPosition = leftParenthesisPosition;
        this.rightParenthesisPosition = rightParenthesisPosition;
        this.commaFilePositions = commaFilePositions;
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

    public int getLineNumber() {
        return lineNumber;
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

    public FilePosition getLeftParenthesisPosition() {
        return leftParenthesisPosition;
    }

    public FilePosition getRightParenthesisPosition() {
        return rightParenthesisPosition;
    }

    public List<FilePosition> getCommaFilePositions() {
        return commaFilePositions;
    }

    public String getMethodIdentifier() {
        Expression expr = methodName;

        while (expr instanceof ParenthesisExpression) { // unwind any parentheses it may be in
            expr = ((ParenthesisExpression) expr).getExpression();
        }
        UnitExpression name = (expr instanceof BinaryExpression)
                ? (UnitExpression) ((BinaryExpression)expr).getRight()
                : (UnitExpression)expr;
        return name.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallExpression that = (MethodCallExpression) o;
        return Objects.equals(methodName, that.methodName)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(path, that.path)
                && Objects.equals(lineNumber, that.lineNumber)
                && Objects.equals(leftParenthesisPosition, that.leftParenthesisPosition)
                && Objects.equals(rightParenthesisPosition, that.rightParenthesisPosition)
                && Objects.equals(commaFilePositions, that.commaFilePositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments, path, lineNumber, leftParenthesisPosition, rightParenthesisPosition,
                commaFilePositions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("methodName", methodName)
                .append("arguments", arguments)
                .append("path", path)
                .append("lineNumber", lineNumber)
                .append("methodSource", methodSource)
                .append("argumentTypes", argumentTypes)
                .append("leftParenthesisPosition", leftParenthesisPosition)
                .append("rightParenthesisPosition", rightParenthesisPosition)
                .append("commaFilePositions", commaFilePositions)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
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
