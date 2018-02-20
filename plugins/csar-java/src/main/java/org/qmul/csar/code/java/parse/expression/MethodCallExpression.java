package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.java.postprocess.methodproc.TypeInstance;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodCallExpression implements Expression {

    private final Expression methodName;
    private final List<Expression> arguments;
    private final Path path;
    private final int lineNumber;
    /**
     * Set during post-processing by {@link org.qmul.csar.code.java.postprocess.methodproc.MethodProcessor}.
     * This is null if unset, or if the source is the same class as the method call is in.
     */
    private TypeInstance methodSource;
    /**
     * Set during post-processing by {@link org.qmul.csar.code.java.postprocess.methodproc.MethodProcessor}.
     */
    private List<TypeInstance> argumentTypes;

    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path, int lineNumber) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.path = path;
        this.lineNumber = lineNumber;
    }

    public MethodCallExpression(Expression methodName, Path path, int lineNumber) {
        this(methodName, new ArrayList<>(), path, lineNumber);
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

    public String getMethodIdentifier() {
        UnitExpression name = (methodName instanceof BinaryExpression)
                ? (UnitExpression) ((BinaryExpression)methodName).getRight()
                : (UnitExpression)methodName;
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
                && Objects.equals(lineNumber, that.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments, path, lineNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("methodName", methodName)
                .append("arguments", arguments)
                .append("path", path)
                .append("lineNo", lineNumber)
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
