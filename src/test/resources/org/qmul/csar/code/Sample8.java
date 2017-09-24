enum SimpleOperators implements Operator<Integer> {
    PLUS {
        Integer apply(Integer a, Integer b) {
            return a + b;
        }
    },
    MINUS {
        Integer apply(Integer a, Integer b) {
            return a - b;
        }
    };
}
