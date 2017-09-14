package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SwitchControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Expression argument;
    private final List<Statement> elements;

    public SwitchControlFlowLanguageElement(Expression argument, List<Statement> elements) {
        super(ControlFlowType.SWITCH);
        this.argument = argument;
        this.elements = elements;
    }

    public Expression getArgument() {
        return argument;
    }

    public List<Statement> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwitchControlFlowLanguageElement that = (SwitchControlFlowLanguageElement) o;
        return Objects.equals(argument, that.argument) && Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, elements);
    }

    @Override
    public String toString() {
        return String.format("SwitchControlFlowLanguageElement{argument=%s, elements=%s}", argument, elements);
    }

    public interface SwitchElement extends Statement {

    }

    public static class A implements SwitchElement {

    }

    public static class SwitchLabel implements SwitchElement {

        private Optional<Expression> labelExpr;
        private Optional<String> labelIdentifier;

        public SwitchLabel(Optional<Expression> labelExpr, Optional<String> labelIdentifier) {
            this.labelExpr = labelExpr;
            this.labelIdentifier = labelIdentifier;
        }

        public Optional<Expression> getLabelExpr() {
            return labelExpr;
        }

        public Optional<String> getLabelIdentifier() {
            return labelIdentifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SwitchLabel that = (SwitchLabel) o;
            return Objects.equals(labelExpr, that.labelExpr) && Objects.equals(labelIdentifier, that.labelIdentifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(labelExpr, labelIdentifier);
        }

        @Override
        public String toString() {
            return String.format("SwitchLabel{labelExpr=%s, labelIdentifier=%s}", labelExpr, labelIdentifier);
        }
    }
}
