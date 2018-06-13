public class A {

    static {
        A a = new A();
        a.print(a.print(1, 2), 2);
    }

    public void print(int a, int b) {
        System.out.println("print in A");
    }
}
