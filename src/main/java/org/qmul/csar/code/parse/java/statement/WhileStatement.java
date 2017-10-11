package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A while statement.
 */
public class WhileStatement implements Statement {

    private final Expression condition;
    private final Statement statement;

    public WhileStatement(Expression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileStatement that = (WhileStatement) o;
        return Objects.equals(condition, that.condition)
                && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statement);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("condition", condition)
                .append("statement", statement)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%swhile %s {%s%s%s%s}", StringUtils.indentation(indentation), condition.toPseudoCode(),
                StringUtils.LINE_SEPARATOR, statement.toPseudoCode(indentation + 1), StringUtils.LINE_SEPARATOR,
                StringUtils.indentation(indentation));
    }
}
