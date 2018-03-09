package base;

public class Y extends U {

    public void testSuperMethodCall() {
        super.add5(1, 2);
    }

    public void testThisMethodCall() {
        this.add6(1, 2);
    }

    public void testSuperVariableMethodCall() {
        super.z.test1();
    }

    public void testThisVariableMethodCall() {
        this.z.test2();
    }
}
