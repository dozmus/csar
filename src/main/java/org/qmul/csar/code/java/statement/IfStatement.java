package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * An if statement.
 */
public class IfStatement implements Statement {

    private final Expression condition;
    private final Statement statement;
    private final Optional<Statement> elseStatement;

    public IfStatement(Expression condition, Statement statement, Optional<Statement> elseStatement) {
        this.condition = condition;
        this.statement = statement;
        this.elseStatement = elseStatement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getStatement() {
        return statement;
    }

    public Optional<Statement> getElseStatement() {
        return elseStatement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfStatement that = (IfStatement) o;
        return Objects.equals(condition, that.condition)
                && Objects.equals(statement, that.statement)
                && Objects.equals(elseStatement, that.elseStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statement, elseStatement);
    }

    @Override
    public String toString() {
        return String.format("IfStatement{condition=%s, statement=%s, elseStatement=%s}", condition, statement,
                elseStatement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder sb = new StringBuilder()
                .append("if (")
                .append(condition.toPseudoCode())
                .append(") {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(statement.toPseudoCode())
                .append(StringUtils.LINE_SEPARATOR)
                .append("}");
        elseStatement.ifPresent(statement -> sb.append(" else {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(statement.toPseudoCode())
                .append(StringUtils.LINE_SEPARATOR)
                .append("}"));
        return sb.toString();
    }
}
