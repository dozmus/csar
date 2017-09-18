package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class ArrayAccessExpression implements Expression {

    private final Expression array;
    private final Expression index;

    public ArrayAccessExpression(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayAccessExpression that = (ArrayAccessExpression) o;
        return Objects.equals(array, that.array) && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(array, index);
    }

    @Override
    public String toString() {
        return String.format("ArrayAccessExpression{array=%s, index=%s}", array, index);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + array.toPseudoCode() + "[" + index.toPseudoCode() + "]";
    }
}
