package org.qmul.csar.code.java.expression;

public enum BinaryOperation {
    MUL("*"), DIV("/"), MOD("%"), ADD("+"), SUB("-"),
    LE("<="), GE(">="), GT(">"), LT("<"),
    INSTANCE_OF("instanceof"),
    EQUAL("=="), NOTEQUAL("!="),
    BITAND("&"), BITOR("|"), BITXOR("^"),
    AND("&&"), OR("||"),
    ASSIGN("="),
    ADD_ASSIGN("+="), SUB_ASSIGN("-="), MUL_ASSIGN("*="), DIV_ASSIGN("/="), AND_ASSIGN("&="), OR_ASSIGN("|="),
    XOR_ASSIGN("^="), RSHIFT_ASSIGN(">>="), LSHIFT_ASSIGN("<<="), MOD_ASSIGN("%="),
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
}
