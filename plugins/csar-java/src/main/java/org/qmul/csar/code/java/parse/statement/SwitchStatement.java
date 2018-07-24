package org.qmul.csar.code.java.parse.statement;

import org.qmul.csar.code.java.parse.expression.ParenthesisExpression;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;

/**
 * A switch statement.
 */
public class SwitchStatement implements Statement {

    /**
     * This is a {@link ParenthesisExpression}, since the argument is expressed
     * within parentheses.
     */
    private Expression argument;
    private BlockStatement block;

    public SwitchStatement() {
    }

    public SwitchStatement(Expression argument, BlockStatement block) {
        this.argument = argument;
        this.block = block;
    }

    public Expression getArgument() {
        return argument;
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwitchStatement that = (SwitchStatement) o;
        return Objects.equals(argument, that.argument)
                && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, block);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("argument", argument)
                .append("block", block)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        String i = StringUtils.indentation(indentation);
        return String.format("%sswitch(%s) {%s%s%s%s}", i, argument.toPseudoCode(), System.lineSeparator(),
                block.toPseudoCode(indentation + 1), System.lineSeparator(), i);
    }
}
