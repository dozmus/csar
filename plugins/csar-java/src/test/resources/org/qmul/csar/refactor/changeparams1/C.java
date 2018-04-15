public class C {

    static {
        A a = new A();
        a.print(1, 2);
    }

    public C() {
        B b = new B();
        b.print(500, 100);
    }
}
