package base;

public class SumImpl5 {

    public void method() {
        class InnerImpl implements SumInterface {

            public int add(int a, int b) {
                return a + b;
            }
        }
    }
}
