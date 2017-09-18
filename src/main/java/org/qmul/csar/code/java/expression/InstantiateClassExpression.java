package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.code.java.statement.ClassStatement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InstantiateClassExpression implements Expression {

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
    public String toPseudoCode(int indentation) {
        return "instantiate_class"; // TODO write
    }
}
