public class A {

    static {
        A a = new A();
        a.print(1, a.print(1, 2));
    }

    public void print(int a, int b) {
        System.out.println("print in A");
    }
}
