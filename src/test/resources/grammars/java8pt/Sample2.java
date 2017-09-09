package grammars.java8pt;

import java.lang.Runnable;

public interface Sample2 extends Runnable {

    public default void print(String s) {
        System.out.println(s);
    }

    void print(int level, String... s);

    void print(String[] $);
}
