package org.qmul.csar.code.java.statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TryWithResourcesStatement extends TryStatement {

    private final List<LocalVariableStatement> resources;

    public TryWithResourcesStatement(BlockStatement block, List<CatchStatement> catches, BlockStatement finallyBlock,
            List<LocalVariableStatement> resources) {
        super(block, catches, finallyBlock);
        this.resources = Collections.unmodifiableList(resources);
    }

    public List<LocalVariableStatement> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TryWithResourcesStatement that = (TryWithResourcesStatement) o;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "try-with-res"; // TODO write
    }
}
