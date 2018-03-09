package base;

public class A {

    public int add(int a, int b) {
        return a + b;
    }

    private void testSameClassInstanceMethodCall() {
        add(1, 2);
        add();
        staticAdd();
    }

    private void testSameClassRecursiveInstanceMethodCall() {
        testSameClassRecursiveInstanceMethodCall();
    }

    private void testSameClassStaticMethodCall() {
        staticAdd();
    }

    public static void staticAdd() {
    }
}
