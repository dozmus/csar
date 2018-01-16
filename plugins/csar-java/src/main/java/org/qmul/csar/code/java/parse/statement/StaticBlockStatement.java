package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("block", block)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%sstatic {%s%s%s%s}", StringUtils.indentation(indentation), System.lineSeparator(),
                block.toPseudoCode(indentation + 1), System.lineSeparator(), StringUtils.indentation(indentation));
    }
}
