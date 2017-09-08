package grammars.java8pt;

import java.lang.Runnable;

public interface Sample2 implements Runnable {

    void print(String s);

    void print(int level, String... s);

    void print(String[] $);
}
