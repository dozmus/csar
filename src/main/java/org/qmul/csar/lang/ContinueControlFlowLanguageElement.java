package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class ContinueControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Optional<String> identifier;

    public ContinueControlFlowLanguageElement(Optional<String> identifier) {
        super(ControlFlowType.CONTINUE);
        this.identifier = identifier;
    }

    public Optional<String> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinueControlFlowLanguageElement that = (ContinueControlFlowLanguageElement) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return String.format("ContinueControlFlowLanguageElement{identifier=%s}", identifier);
    }
}
