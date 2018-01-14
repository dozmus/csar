package base;

public class Y extends U {

    public void otherAdd1() {
        super.add5(1, 2);
    }

    public void otherAdd2() {
        this.add6(1, 2);
    }

    public void otherAdd3() {
        super.z.test1();
    }

    public void otherAdd4() {
        this.z.test2();
    }
}
