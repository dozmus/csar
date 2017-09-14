package org.qmul.csar.lang;

import java.util.Objects;

public class WhileControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression condition;
    private final Statement statement;

    public WhileControlFlowLanguageElement(Expression condition, Statement statement) {
        super(ControlFlowType.WHILE);
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
        WhileControlFlowLanguageElement that = (WhileControlFlowLanguageElement) o;
        return Objects.equals(condition, that.condition) && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statement);
    }

    @Override
    public String toString() {
        return String.format("WhileControlFlowLanguageElement{condition=%s, statement=%s}", condition, statement);
    }
}
