package org.qmul.csar.query.domain;

public class ControlFlowLanguageElement extends LanguageElement {

    // TODO finish

    private final ControlFlowType controlFlowType;

    public ControlFlowLanguageElement(ControlFlowType controlFlowType) {
        this.controlFlowType = controlFlowType;
    }

    public ControlFlowLanguageElement(Type type, String identifierName, ControlFlowType controlFlowType) {
        super(type, identifierName);
        this.controlFlowType = controlFlowType;
    }

    public ControlFlowType getControlFlowType() {
        return controlFlowType;
    }

    public enum ControlFlowType {
        FOR, TERNARY, FOREACH
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ControlFlowLanguageElement that = (ControlFlowLanguageElement) o;
        return controlFlowType == that.controlFlowType;
    }
}
