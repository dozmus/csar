package org.qmul.csar.code.java.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class UnitExpression implements Expression {

    private final ValueType valueType;
    private final String value;

    public UnitExpression(ValueType valueType, String value) {
        this.valueType = valueType;
        this.value = value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitExpression that = (UnitExpression) o;
        return valueType == that.valueType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueType, value);
    }

    @Override
    public String toString() {
        return String.format("UnitExpression{type=%s, value='%s'}", valueType, value);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + value;
    }

    public enum ValueType {
        LITERAL, IDENTIFIER, CLASS_REFERENCE, METHOD_REFERENCE, SUPER, THIS, THIS_CALL, SUPER_CALL, TYPE, NEW,
        METHOD_CALL
    }
}