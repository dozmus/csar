package base;

public class V {

    public static void testLocalVariableMethodCall() {
        U u = null;
        u.add2(1, 2);
    }

    public static void testParameterVariableMethodCall(U u) {
        u.add3(1, 2);
    }
}
