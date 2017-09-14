package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class IfControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression condition;
    private final Statement statement;
    private final Optional<Statement> elseStatement;

    public IfControlFlowLanguageElement(Expression condition, Statement statement, Optional<Statement> elseStatement) {
        super(ControlFlowType.IF);
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
        if (!super.equals(o)) return false;
        IfControlFlowLanguageElement that = (IfControlFlowLanguageElement) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(statement, that.statement) &&
                Objects.equals(elseStatement, that.elseStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), condition, statement, elseStatement);
    }

    @Override
    public String toString() {
        return String.format("IfControlFlowLanguageElement{condition=%s, statement=%s, elseStatement=%s} %s", condition,
                statement, elseStatement, super.toString());
    }
}
