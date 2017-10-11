package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * An if statement.
 */
public class IfStatement implements Statement {

    /**
     * This is a {@link org.qmul.csar.code.parse.java.expression.ParenthesisExpression}, since the condition is expressed
     * within parentheses.
     */
    private final Expression condition;
    /**
     * This is either a {@link ExpressionStatement} or a {@link BlockStatement}.
     */
    private final Statement statement;
    /**
     * This can contain an {@link IfStatement} (for else-if), or an {@link ExpressionStatement} or a
     * {@link BlockStatement} (for else).
     */
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
        return new ToStringBuilder(this)
                .append("condition", condition)
                .append("statement", statement)
                .append("elseStatement", elseStatement)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder sb = new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append("if ")
                .append(condition.toPseudoCode())
                .append(" {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(statement.toPseudoCode(indentation + 1))
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}");
        elseStatement.ifPresent(statement -> sb.append(" else {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(statement.toPseudoCode(indentation + 1))
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}"));
        return sb.toString();
    }
}
