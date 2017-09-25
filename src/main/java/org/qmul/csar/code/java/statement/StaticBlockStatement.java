package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A static block.
 */
public class StaticBlockStatement implements Statement {

    private final BlockStatement block;

    public StaticBlockStatement(BlockStatement block) {
        this.block = block;
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticBlockStatement that = (StaticBlockStatement) o;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block);
    }

    @Override
    public String toString() {
        return String.format("StaticBlockStatement{block=%s}", block);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%sstatic {%s%s%s%s}", StringUtils.indentation(indentation), StringUtils.LINE_SEPARATOR,
                block.toPseudoCode(indentation + 1), StringUtils.LINE_SEPARATOR, StringUtils.indentation(indentation));
    }
}
