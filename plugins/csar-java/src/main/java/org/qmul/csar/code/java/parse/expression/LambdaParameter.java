package org.qmul.csar.code.java.parse.expression;

import org.qmul.csar.code.java.parse.statement.ParameterVariableStatement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface LambdaParameter extends Expression {
    
    class Identifier implements LambdaParameter {

        private String identifier;

        public Identifier() {
        }

        public Identifier(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identifier that = (Identifier) o;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + identifier;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                    .append("identifier", identifier)
                    .toString();
        }
    }

    class Identifiers implements LambdaParameter {

        private List<String> identifiers;

        public Identifiers() {
        }

        public Identifiers(List<String> identifiers) {
            this.identifiers = Collections.unmodifiableList(identifiers);
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identifiers that = (Identifiers) o;
            return Objects.equals(identifiers, that.identifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifiers);
        }

        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + String.join(", ", identifiers);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                    .append("identifiers", identifiers)
                    .toString();
        }
    }

    class ParameterVariables implements LambdaParameter {
        
        private final List<ParameterVariableStatement> variables;

        public ParameterVariables() {
            this(new ArrayList<>());
        }

        public ParameterVariables(List<ParameterVariableStatement> variables) {
            this.variables = Collections.unmodifiableList(variables);
        }

        public List<ParameterVariableStatement> getVariables() {
            return variables;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParameterVariables that = (ParameterVariables) o;
            return Objects.equals(variables, that.variables);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variables);
        }

        @Override
        public String toPseudoCode(int indentation) {
            return new StringBuilder()
                    .append(StringUtils.indentation(indentation))
                    .append("(")
                    .append(String.join(", ",
                            variables.stream().map(SerializableCode::toPseudoCode).collect(Collectors.toList())))
                    .append(")")
                    .toString();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                    .append("variables", variables)
                    .toString();
        }
    }
}
