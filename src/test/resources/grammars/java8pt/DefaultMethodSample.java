interface DefaultMethodSample {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}
