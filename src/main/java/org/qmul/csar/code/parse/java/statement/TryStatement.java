package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A try statement, however this cannot represent a try with resources statement.
 * @see TryWithResourcesStatement
 */
public class TryStatement implements Statement {

    private final BlockStatement block;
    private final List<CatchStatement> catches;
    private final Optional<BlockStatement> finallyBlock;

    public TryStatement(BlockStatement block, List<CatchStatement> catches, Optional<BlockStatement> finallyBlock) {
        this.block = block;
        this.catches = Collections.unmodifiableList(catches);
        this.finallyBlock = finallyBlock;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public List<CatchStatement> getCatches() {
        return catches;
    }

    public Optional<BlockStatement> getFinallyBlock() {
        return finallyBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TryStatement that = (TryStatement) o;
        return Objects.equals(block, that.block)
                && Objects.equals(catches, that.catches)
                && Objects.equals(finallyBlock, that.finallyBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, catches, finallyBlock);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("block", block)
                .append("catches", catches)
                .append("finallyBlock", finallyBlock)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append("try {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(block.toPseudoCode(indentation + 1))
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}");

        // Catches
        for (CatchStatement catchSt : catches) {
            builder.append(catchSt.toPseudoCode(indentation));
        }

        // Finally
        finallyBlock.ifPresent(blockStatement -> builder.append(" finally {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(blockStatement.toPseudoCode(indentation + 1))
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}"));
        return builder.toString();
    }
}
