package org.qmul.io;

public final class ConsoleTextPrinter extends TextPrinter {

    @Override
    public boolean print(String text) {
        System.out.println(text);
        return true;
    }
}
