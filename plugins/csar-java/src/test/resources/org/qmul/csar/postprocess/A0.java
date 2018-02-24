package base;

public class A0 {

    public void testMethodCallWithSubtypeArgumentValue1() {
        Class1 c = new Class1();
        B b = null;
        c.method2(b);
    }

    public void testMethodCallWithSubtypeArgumentValue2() {
        Class1 c = new Class1();
        c.method2(getB());
    }

    public void testMethodCallWithSubtypeArgumentValue3() {
        Class1 c = new Class1();
        c.method2(this.getB());
    }

    private B getB() {
        return new B();
    }
}
