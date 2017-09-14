package org.qmul.csar.lang;

import java.util.Objects;

public class DoWhileControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression condition;
    private final Statement statement;

    public DoWhileControlFlowLanguageElement(Expression condition, Statement statement) {
        super(ControlFlowType.DO_WHILE);
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
        DoWhileControlFlowLanguageElement that = (DoWhileControlFlowLanguageElement) o;
        return Objects.equals(condition, that.condition) && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statement);
    }

    @Override
    public String toString() {
        return String.format("DoWhileControlFlowLanguageElement{condition=%s, statement=%s}", condition, statement);
    }
}
