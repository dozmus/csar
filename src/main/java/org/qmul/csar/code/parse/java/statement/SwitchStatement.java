package org.qmul.csar.code.parse.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A switch statement.
 */
public class SwitchStatement implements Statement {

    /**
     * This is a {@link org.qmul.csar.code.parse.java.expression.ParenthesisExpression}, since the argument is expressed
     * within parentheses.
     */
    private final Expression argument;
    private final BlockStatement block;

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
        return String.format("SwitchStatement{argument=%s, block=%s}", argument, block);
    }

    @Override
    public String toPseudoCode(int indentation) {
        String i = StringUtils.indentation(indentation);
        return String.format("%sswitch(%s) {%s%s%s%s}", i, argument.toPseudoCode(), StringUtils.LINE_SEPARATOR,
                block.toPseudoCode(indentation + 1), StringUtils.LINE_SEPARATOR, i);
    }
}
