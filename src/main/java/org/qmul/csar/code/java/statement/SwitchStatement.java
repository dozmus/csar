package org.qmul.csar.code.java.statement;

import org.qmul.csar.code.java.expression.UnitExpression;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class SwitchStatement implements Statement {

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
        return String.format("switch(%s) {%s%s%s}", argument.toPseudoCode(), StringUtils.LINE_SEPARATOR, block.toPseudoCode(),
                StringUtils.LINE_SEPARATOR);
    }

    public static class SwitchLabelStatement implements Statement {

        private Expression labelExpression;

        public SwitchLabelStatement(String literal) { // NOTE 'default' is placed here, if necessary
            this(new UnitExpression(UnitExpression.Type.LITERAL, literal));
        }

        public SwitchLabelStatement(Expression labelExpression) {
            this.labelExpression = labelExpression;
        }

        public Expression getLabelExpr() {
            return labelExpression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SwitchLabelStatement that = (SwitchLabelStatement) o;
            return Objects.equals(labelExpression, that.labelExpression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(labelExpression);
        }

        @Override
        public String toString() {
            return String.format("SwitchLabelStatement{labelExpression=%s}", labelExpression);
        }

        @Override
        public String toPseudoCode(int indentation) {
            return labelExpression.toPseudoCode() + ":";
        }
    }
}
