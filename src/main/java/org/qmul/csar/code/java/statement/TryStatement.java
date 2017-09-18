package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TryStatement implements Statement {

    private final BlockStatement block;
    private final List<CatchStatement> catches;
    private final BlockStatement finallyBlock;

    public TryStatement(BlockStatement block, List<CatchStatement> catches, BlockStatement finallyBlock) {
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

    public BlockStatement getFinallyBlock() {
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
    public String toPseudoCode(int indentation) {
        return "try"; // TODO write
    }
}
