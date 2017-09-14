package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;

public class TryResourcesControlFlowLanguageElement extends TryControlFlowLanguageElement {

    private final List<VariableLanguageElement> resources;

    public TryResourcesControlFlowLanguageElement(List<VariableLanguageElement> resources, List<Statement> block,
            List<CatchControlFlowLanguageElement> catches, List<Statement> finallyBlock) {
        super(ControlFlowType.TRY_RESOURCES, block, catches, finallyBlock);
        this.resources = resources;
    }

    public List<VariableLanguageElement> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TryResourcesControlFlowLanguageElement that = (TryResourcesControlFlowLanguageElement) o;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toString() {
        return String.format("TryResourcesControlFlowLanguageElement{resources=%s} %s", resources, super.toString());
    }
}
