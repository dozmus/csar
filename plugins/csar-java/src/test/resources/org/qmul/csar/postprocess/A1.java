package base;

public class A1 {

    public void testStaticMethodCallOnClassName() {
        V.testLocalVariableMethodCall();
    }

    public void testStaticMethodCallOnFullyQualifiedName() {
        base.V.testLocalVariableMethodCall();
    }

    public void testMethodCallWithNestedParameterArguments(int a, int b) {
        A adder = new A();

        class Inner {
            int add(int c, int d) {
                return adder.add(a, b);
            }
        }
    }

    public void testMethodCallWithNewInstantiation() {
        new A().add(1, 2);
    }

    public void testMethodCallWithLocalVariableArgument() {
        A adder = new A();
        int a = 3;

        class Inner {
            int add(int c, int d) {
                return adder.add(a, d);
            }
        }
    }

    public void testMethodCallWithLocalVariableArgumentFromForEachLoop() {
        A adder = new A();

        for (int i : integers) {
            adder.add(i, 5);
        }
    }
}
