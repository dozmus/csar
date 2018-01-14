package base;

public class A {

    public int add(int a, int b) {
        return a + b;
    }

    private void add() {
        add(1, 2);
        add();
        staticAdd();
    }

    public static void staticAdd() {
    }
}
