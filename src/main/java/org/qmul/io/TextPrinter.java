package org.qmul.io;

public abstract class TextPrinter {

    /**
     * Prints text to an arbitrary output.
     * @param text The text to print
     * @return Success
     */
    public abstract boolean print(String text);
}
