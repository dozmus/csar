package org.qmul.csar.lang;

import java.util.Objects;

public class SynchronizedControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression element;
    private final BlockLanguageElement block;

    public SynchronizedControlFlowLanguageElement(Expression element, BlockLanguageElement block) {
        super(ControlFlowType.SYNCHRONIZED);
        this.element = element;
        this.block = block;
    }

    public Expression getElement() {
        return element;
    }

    public BlockLanguageElement getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizedControlFlowLanguageElement that = (SynchronizedControlFlowLanguageElement) o;
        return Objects.equals(element, that.element) && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, block);
    }

    @Override
    public String toString() {
        return String.format("SynchronizedControlFlowLanguageElement{element=%s, block=%s}", element, block);
    }
}
