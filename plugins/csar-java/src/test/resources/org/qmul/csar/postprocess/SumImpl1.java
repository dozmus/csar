package base;

public class SumImpl1 implements SumInterface {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    public int add(String a, String b) {
        return a.length() + b.length();
    }

    public String add(int a, int b) {
        return a + " " + b;
    }
}
