package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A try with resources statement.
 */
public class TryWithResourcesStatement extends TryStatement {

    private LocalVariableStatements resources;

    public TryWithResourcesStatement() {
    }

    public TryWithResourcesStatement(BlockStatement block, List<CatchStatement> catches,
                                     Optional<BlockStatement> finallyBlock, LocalVariableStatements resources) {
        super(block, catches, finallyBlock);
        this.resources = resources;
    }

    public LocalVariableStatements getResources() {
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
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("resources", resources)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        // Resource
        List<LocalVariableStatement> locals = resources.getLocals();
        String resourceString = locals.get(0).toPseudoCode(0, false);

        // Try
        StringBuilder builder = new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append("try (")
                .append(resourceString)
                .append(") {")
                .append(System.lineSeparator())
                .append(getBlock().toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}");

        // Catches
        for (CatchStatement catchSt : getCatches()) {
            builder.append(catchSt.toPseudoCode(indentation));
        }

        // Finally
        getFinallyBlock().ifPresent(blockStatement -> builder.append(" finally {")
                .append(System.lineSeparator())
                .append(blockStatement.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}"));
        return builder.toString();
    }
}
