package org.qmul.csar.lang;

import java.util.Objects;

public class LabelControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final String identifier;
    private final Statement statement;

    public LabelControlFlowLanguageElement(String identifier, Statement statement) {
        super(ControlFlowType.LABEL);
        this.identifier = identifier;
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LabelControlFlowLanguageElement that = (LabelControlFlowLanguageElement) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, statement);
    }

    @Override
    public String toString() {
        return String.format("LabelControlFlowLanguageElement{identifier='%s', statement=%s} %s", identifier, statement,
                super.toString());
    }
}
