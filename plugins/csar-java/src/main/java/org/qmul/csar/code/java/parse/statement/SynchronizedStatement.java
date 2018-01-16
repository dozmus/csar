package org.qmul.csar.code.java.parse.statement;

import org.qmul.csar.code.java.parse.expression.ParenthesisExpression;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A synchronized statement.
 */
public class SynchronizedStatement implements Statement {

    /**
     * This is a {@link ParenthesisExpression}, since the element to lock is
     * expressed within parentheses.
     */
    private final Expression element;
    private final BlockStatement block;

    public SynchronizedStatement(Expression element, BlockStatement block) {
        this.element = element;
        this.block = block;
    }

    public Expression getElement() {
        return element;
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizedStatement that = (SynchronizedStatement) o;
        return Objects.equals(element, that.element)
                && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, block);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("element", element)
                .append("block", block)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append("synchronized(")
                .append(element.toPseudoCode())
                .append(") {")
                .append(System.lineSeparator())
                .append(block.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}")
                .toString();
    }
}
