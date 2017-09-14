package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;

public class TryControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final List<Statement> block;
    private final List<CatchControlFlowLanguageElement> catches;
    private final List<Statement> finallyBlock;

    public TryControlFlowLanguageElement(List<Statement> block, List<CatchControlFlowLanguageElement> catches,
            List<Statement> finallyBlock) {
        this(ControlFlowType.TRY, block, catches, finallyBlock);
    }

    protected TryControlFlowLanguageElement(ControlFlowType type, List<Statement> block,
            List<CatchControlFlowLanguageElement> catches, List<Statement> finallyBlock) {
        super(type);
        this.block = block;
        this.catches = catches;
        this.finallyBlock = finallyBlock;
    }

    public List<Statement> getBlock() {
        return block;
    }

    public List<CatchControlFlowLanguageElement> getCatches() {
        return catches;
    }

    public List<Statement> getFinallyBlock() {
        return finallyBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TryControlFlowLanguageElement that = (TryControlFlowLanguageElement) o;
        return Objects.equals(block, that.block) &&
                Objects.equals(catches, that.catches) &&
                Objects.equals(finallyBlock, that.finallyBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, catches, finallyBlock);
    }

    @Override
    public String toString() {
        return String.format("TryControlFlowLanguageElement{block=%s, catches=%s, finallyBlock=%s}", block, catches,
                finallyBlock);
    }
}
