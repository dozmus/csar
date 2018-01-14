import java.lang.Runnable;

public interface Sample2 extends Runnable {

    public static int ITERATIONS = 1000;

    public default void print(String s) {
        System.out.println(s);
    }

    <E> void print(E level, String... s);

    String[] name = generateName(Sample2.class);

    void print(String[] $);
}
