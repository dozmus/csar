package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class BreakControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Optional<String> identifier;

    public BreakControlFlowLanguageElement(Optional<String> identifier) {
        super(ControlFlowType.BREAK);
        this.identifier = identifier;
    }

    public Optional<String> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreakControlFlowLanguageElement that = (BreakControlFlowLanguageElement) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return String.format("BreakControlFlowLanguageElement{identifier=%s}", identifier);
    }
}
