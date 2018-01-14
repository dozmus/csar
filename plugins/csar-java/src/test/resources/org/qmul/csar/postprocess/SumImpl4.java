package base;

public class SumImpl4 {

    public class InnerImpl implements SumInterface {

        public int add(int a, int b) {
            return a + b;
        }
    }
}
