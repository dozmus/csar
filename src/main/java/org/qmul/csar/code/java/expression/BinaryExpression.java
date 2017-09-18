package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class BinaryExpression implements Expression {

    private final Expression left;
    private final BinaryOperation op;
    private final Expression right;

    public BinaryExpression(Expression left, BinaryOperation op, Expression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public BinaryOperation getOp() {
        return op;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExpression that = (BinaryExpression) o;
        return Objects.equals(left, that.left)
                && op == that.op
                && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, op, right);
    }

    @Override
    public String toString() {
        return String.format("BinaryExpression{left=%s, op=%s, right=%s}", left, op, right);
    }

    @Override
    public String toPseudoCode(int indentation) {
        if (op != BinaryOperation.DOT) {
            return String.format("%s%s %s %s", StringUtils.indentation(indentation), left.toPseudoCode(),
                    op.getSymbol(), right.toPseudoCode());
        } else {
            return String.format("%s%s%s%s", StringUtils.indentation(indentation), left.toPseudoCode(),
                    op.getSymbol(), right.toPseudoCode());
        }
    }
}
