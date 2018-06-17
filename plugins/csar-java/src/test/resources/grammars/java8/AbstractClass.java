// Source: https://en.wikipedia.org/wiki/Java_syntax

/* This is a multi-line comment.
It may occupy more than one line. */

// This is an end-of-line comment

/**
 * This is a documentation comment.
 *
 * @author John Doe
 */
package grammars.java8;

import static screen.ColorName.*;

public class AbstractClass {
    private static final String hello;

    static {
        System.out.println(AbstractClass.class.getName() + ": static block runtime");
        hello = "hello from " + AbstractClass.class.getName();
    }

    {
        System.out.println(AbstractClass.class.getName() + ": instance block runtime");
    }

    public AbstractClass() {
        System.out.println(AbstractClass.class.getName() + ": constructor runtime");
    }

    public static void hello() {
        System.out.println(hello);
    }

    public void doubleLiteralInHex() {
        double h = 0x1.0p20;
        double h = 0x1.0p0;
        double h = 0x10p20;
        double h = 0x10p0;
    }
}
