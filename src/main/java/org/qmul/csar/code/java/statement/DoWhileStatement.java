package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A do-while statement.
 */
public class DoWhileStatement implements Statement {

    private final Expression condition;
    private final Statement statement;

    public DoWhileStatement(Expression condition, Statement statement) {
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
        DoWhileStatement that = (DoWhileStatement) o;
        return Objects.equals(condition, that.condition)
                && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statement);
    }

    @Override
    public String toString() {
        return String.format("DoWhileStatement{condition=%s, statement=%s}", condition, statement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%sdo {%s%s%s} while(%s);", StringUtils.indentation(indentation),
                StringUtils.LINE_SEPARATOR, statement.toPseudoCode(), StringUtils.LINE_SEPARATOR,
                condition.toPseudoCode());
    }
}
