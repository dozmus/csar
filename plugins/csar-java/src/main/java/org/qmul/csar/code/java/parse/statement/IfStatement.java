package org.qmul.csar.code.java.parse.statement;

import org.qmul.csar.code.java.parse.expression.ParenthesisExpression;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

/**
 * An if statement.
 */
public class IfStatement implements Statement {

    /**
     * This is a {@link ParenthesisExpression}, since the condition is expressed
     * within parentheses.
     */
    private Expression condition;
    /**
     * This is either a {@link ExpressionStatement} or a {@link BlockStatement}.
     */
    private Statement statement;
    /**
     * This can contain an {@link IfStatement} (for else-if), or an {@link ExpressionStatement} or a
     * {@link BlockStatement} (for else).
     */
    private Optional<Statement> elseStatement;

    public IfStatement() {
    }

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
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
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
                .append(System.lineSeparator())
                .append(statement.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}");
        elseStatement.ifPresent(statement -> {
            if (statement instanceof IfStatement) {
                sb.append(" else ").append(statement.toPseudoCode(indentation).trim());
                return;
            }
            sb.append(" else {")
                    .append(System.lineSeparator())
                    .append(statement.toPseudoCode(indentation + 1))
                    .append(System.lineSeparator())
                    .append(StringUtils.indentation(indentation))
                    .append("}");
        });
        return sb.toString();
    }
}
