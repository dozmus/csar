package org.qmul.csar.code.java.parse.expression;

public enum BinaryOperation {
    MUL("*"), DIV("/"), MOD("%"), ADD("+"), SUB("-"),
    LE("<="), GE(">="), GT(">"), LT("<"),
    INSTANCE_OF("instanceof"),
    EQUALS("=="), NOTEQUALS("!="),
    BITAND("&"), BITOR("|"), BITXOR("^"),
    AND("&&"), OR("||"),
    ASSIGN("="),
    ADD_ASSIGN("+="), SUB_ASSIGN("-="), MUL_ASSIGN("*="), DIV_ASSIGN("/="), AND_ASSIGN("&="), OR_ASSIGN("|="),
    XOR_ASSIGN("^="), RSHIFT_ASSIGN(">>="), LSHIFT_ASSIGN("<<="), UNSIGNED_RSHIFT_ASSIGN(">>>="), MOD_ASSIGN("%="),
    LSHIFT("<<"), RSHIFT(">>"), UNSIGNED_RSHIFT(">>>"),
    DOT("."),
    QUESTION("?");

    private final String symbol;

    BinaryOperation(String symbol) {
        this.symbol = symbol;
    }

    public static BinaryOperation forSymbol(String symbol) {
        for (BinaryOperation op : values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isArithmeticOperation() {
        return this == MUL || this == DIV || this == MOD || this == ADD || this == SUB || this == LSHIFT
                || this == RSHIFT || this == UNSIGNED_RSHIFT || this == BITAND || this == BITOR || this == BITXOR;
    }

    public boolean isArithmeticAssignOperation() {
        return this == ASSIGN || this == ADD_ASSIGN || this == SUB_ASSIGN || this == MUL_ASSIGN || this == DIV_ASSIGN
                || this == RSHIFT_ASSIGN || this == LSHIFT_ASSIGN || this == UNSIGNED_RSHIFT_ASSIGN || this == AND_ASSIGN
                || this == XOR_ASSIGN || this == OR_ASSIGN || this == MOD_ASSIGN;
    }

    public boolean isBoolean() {
        return this == AND || this == OR || this == EQUALS || this == NOTEQUALS || this == LE || this == GE
                || this == GT || this == LT;
    }
}
