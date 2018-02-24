package base;

public class A1 {

    public void testStaticMethodCallOnClassName() {
        V.staticAdd2();
    }

    public void testStaticMethodCallOnFullyQualifiedName() {
        base.V.staticAdd2();
    }
}
