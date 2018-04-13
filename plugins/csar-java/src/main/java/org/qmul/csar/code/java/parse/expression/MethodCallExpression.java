package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

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
    private int lParenStartIdx;
    private int rParenStartIdx;
    private List<Integer> commaStartIndexes;

    public MethodCallExpression(Expression methodName, List<Expression> arguments, Path path, int lineNumber,
            int lParenStartIdx, int rParenStartIdx, List<Integer> commaStartIndexes) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.path = path;
        this.lineNumber = lineNumber;
        this.lParenStartIdx = lParenStartIdx;
        this.rParenStartIdx = rParenStartIdx;
        this.commaStartIndexes = commaStartIndexes;
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
        Expression expr = methodName;

        while (expr instanceof ParenthesisExpression) { // unwind any parentheses it may be in
            expr = ((ParenthesisExpression) expr).getExpression();
        }
        UnitExpression name = (expr instanceof BinaryExpression)
                ? (UnitExpression) ((BinaryExpression)expr).getRight()
                : (UnitExpression)expr;
        return name.getValue();
    }

    public int getlParenStartIdx() {
        return lParenStartIdx;
    }

    public int getrParenStartIdx() {
        return rParenStartIdx;
    }

    public List<Integer> getCommaStartIndexes() {
        return commaStartIndexes;
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
                && Objects.equals(lParenStartIdx, that.lParenStartIdx)
                && Objects.equals(rParenStartIdx, that.rParenStartIdx)
                && Objects.equals(commaStartIndexes, that.commaStartIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments, path, lineNumber, lParenStartIdx, rParenStartIdx, commaStartIndexes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("methodName", methodName)
                .append("arguments", arguments)
                .append("path", path)
                .append("lineNo", lineNumber)
                .append("lParenStartIdx", lParenStartIdx)
                .append("rParenStartIdx", rParenStartIdx)
                .append("commaStartIndexes", commaStartIndexes)
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
