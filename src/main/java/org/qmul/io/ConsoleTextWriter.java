package org.qmul.io;

public final class ConsoleTextWriter extends TextPrinter {

    @Override
    public boolean write(String results) {
        System.out.println(results);
        return true;
    }
}
