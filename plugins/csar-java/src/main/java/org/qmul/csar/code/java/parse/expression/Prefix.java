package org.qmul.csar.code.java.parse.expression;

public enum Prefix {
    ADD("+"), SUB("-"), INC("++"), DEC("--"), TILDE("~"), BANG("!");

    private final String symbol;

    Prefix(String symbol) {
        this.symbol = symbol;
    }

    public static Prefix forSymbol(String symbol) {
        for (Prefix op : values()) {
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
