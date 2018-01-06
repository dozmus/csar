package org.qmul.csar.code.parse.java.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodCallExpression implements Expression {

    private final Expression methodName;
    private final List<Expression> arguments;
    private final int lineNumber;

    public MethodCallExpression(Expression methodName, List<Expression> arguments, int lineNumber) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.lineNumber = lineNumber;
    }

    public MethodCallExpression(Expression methodName, int lineNumber) {
        this(methodName, new ArrayList<>(), lineNumber);
    }

    public Expression getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallExpression that = (MethodCallExpression) o;
        return Objects.equals(methodName, that.methodName)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(lineNumber, that.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("methodName", methodName)
                .append("arguments", arguments)
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
