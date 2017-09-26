package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodCallExpression implements Expression {

    private final Expression methodName;
    private final List<Expression> arguments;

    public MethodCallExpression(Expression methodName, List<Expression> arguments) {
        this.methodName = methodName;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    public MethodCallExpression(Expression methodName) {
        this(methodName, new ArrayList<>());
    }

    public Expression getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallExpression that = (MethodCallExpression) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, arguments);
    }

    @Override
    public String toString() {
        return String.format("MethodCallExpression{methodName=%s, arguments=%s}", methodName, arguments);
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
