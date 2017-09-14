package org.qmul.csar.lang;

import java.util.Objects;

public class ForEachControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final VariableLanguageElement variable;
    private final Expression collection;
    private final Statement statement;

    public ForEachControlFlowLanguageElement(VariableLanguageElement variable, Expression collection,
            Statement statement) {
        super(ControlFlowType.FOREACH);
        this.variable = variable;
        this.collection = collection;
        this.statement = statement;
    }

    public VariableLanguageElement getVariable() {
        return variable;
    }

    public Expression getCollection() {
        return collection;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForEachControlFlowLanguageElement that = (ForEachControlFlowLanguageElement) o;
        return Objects.equals(variable, that.variable) &&
                Objects.equals(collection, that.collection) &&
                Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, collection, statement);
    }

    @Override
    public String toString() {
        return String.format("ForEachControlFlowLanguageElement{variable=%s, collection=%s, statement=%s}", variable,
                collection, statement);
    }
}
