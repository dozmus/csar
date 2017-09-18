package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.code.java.statement.ExpressionStatement;
import org.qmul.csar.code.java.statement.BlockStatement;
import org.qmul.csar.code.java.statement.ParameterVariableStatement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LambdaExpression implements Expression {

    private final Parameter parameter;
    private final Statement value;

    public LambdaExpression(Parameter parameter, ExpressionStatement value) {
        this.parameter = parameter;
        this.value = value;
    }

    public LambdaExpression(Parameter parameter, BlockStatement value) {
        this.parameter = parameter;
        this.value = value;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Statement getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LambdaExpression that = (LambdaExpression) o;
        return Objects.equals(parameter, that.parameter) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, value);
    }

    @Override
    public String toString() {
        return String.format("LambdaExpression{parameter=%s, value=%s}", parameter, value);
    }

    @Override
    public String toPseudoCode(int indentation) {
        String i = StringUtils.indentation(indentation);

        if (value instanceof ExpressionStatement) {
            return String.format("%s(%s) -> %s", i, parameter.toPseudoCode(), value.toPseudoCode());
        } else {
            return String.format("%s(%s) -> {%s%s%s}", i, parameter.toPseudoCode(), StringUtils.LINE_SEPARATOR,
                    value.toPseudoCode(), StringUtils.LINE_SEPARATOR);
        }
    }

    public interface Parameter extends Expression {
    }

    public static class IdentifierParameter implements Parameter {

        private final String identifier;

        public IdentifierParameter(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + identifier;
        }
    }

    public static class IdentifiersParameter implements Parameter {

        private final List<String> identifiers;

        public IdentifiersParameter(List<String> identifiers) {
            this.identifiers = Collections.unmodifiableList(identifiers);
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + String.join(", ", identifiers);
        }
    }

    public static class ParameterVariablesParameter implements Parameter {
        
        private final List<ParameterVariableStatement> variables;

        public ParameterVariablesParameter(List<ParameterVariableStatement> variables) {
            this.variables = Collections.unmodifiableList(variables);
        }

        public List<ParameterVariableStatement> getVariables() {
            return variables;
        }

        @Override
        public String toPseudoCode(int indentation) {
            return new StringBuilder()
                    .append(StringUtils.indentation(indentation))
                    .append("(")
                    .append(String.join(", ", variables.stream()
                            .map(p -> p.toPseudoCode())
                            .collect(Collectors.toList())))
                    .append(")")
                    .toString();
        }
    }
}
