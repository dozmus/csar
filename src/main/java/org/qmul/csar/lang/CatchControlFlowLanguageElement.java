package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;

public class CatchControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final VariableLanguageElement variable;
    private final List<Statement> block;

    public CatchControlFlowLanguageElement(VariableLanguageElement variable, List<Statement> block) {
        super(ControlFlowType.CATCH);
        this.variable = variable;
        this.block = block;
    }

    public VariableLanguageElement getVariable() {
        return variable;
    }

    public List<Statement> getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatchControlFlowLanguageElement that = (CatchControlFlowLanguageElement) o;
        return Objects.equals(variable, that.variable) && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, block);
    }

    @Override
    public String toString() {
        return String.format("CatchControlFlowLanguageElement{variable=%s, block=%s}", variable, block);
    }
}
