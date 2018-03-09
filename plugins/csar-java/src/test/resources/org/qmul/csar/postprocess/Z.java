package base;

public class Z {

    public U otherAdd1() {
        return null;
    }

    public void testMethodCallOnMethodCall() {
        otherAdd1().add7(1, 2);
    }

    public void test1() {

    }

    public void test2() {

    }

    public void test3() {

    }

    public void test4(int a) {

    }

    int number = 3;

    public class Q {

        public void testMethodCallOnParentInstanceInInnerClass() {
            test3();
        }

        public void testMethodCallWithArgumentInParentInstanceInInnerClass() {
            test4(number);
        }

        public void testMethodCallWithSuperKeywordArgumentInParentInstanceInInnerClass() {
            test4(super.number);
        }

        public void testMethodCallOnSuperKeywordInParentInstanceInInnerClass() {
            super.test4(100);
        }
    }
}
