package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

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
        return String.format("WhileStatement{condition=%s, statement=%s}", condition, statement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%swhile(%s) {%s%s%s}", StringUtils.indentation(indentation), condition.toPseudoCode(),
                StringUtils.LINE_SEPARATOR, statement.toPseudoCode(), StringUtils.LINE_SEPARATOR);
    }
}
