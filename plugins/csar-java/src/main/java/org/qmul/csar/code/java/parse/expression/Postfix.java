package org.qmul.csar.code.java.parse.expression;

public enum Postfix {
    INC("++"), DEC("--");

    private final String symbol;

    Postfix(String symbol) {
        this.symbol = symbol;
    }

    public static Postfix forSymbol(String symbol) {
        for (Postfix op : values()) {
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
