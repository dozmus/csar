package org.qmul.csar.code.refactor;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * A refactor target.
 */
public interface RefactorTarget {

    class Statement implements RefactorTarget {

        private final org.qmul.csar.lang.Statement statement;

        public Statement(org.qmul.csar.lang.Statement statement) {
            this.statement = statement;
        }

        public org.qmul.csar.lang.Statement getStatement() {
            return statement;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Statement that = (Statement) o;
            return Objects.equals(statement, that.statement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(statement);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("statement", statement).toString();
        }
    }

    class Expression implements RefactorTarget {

        private final org.qmul.csar.lang.Expression expression;

        public Expression(org.qmul.csar.lang.Expression expression) {
            this.expression = expression;
        }

        public org.qmul.csar.lang.Expression getExpression() {
            return expression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Statement that = (Statement) o;
            return Objects.equals(expression, that.statement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("expression", expression).toString();
        }
    }
}
