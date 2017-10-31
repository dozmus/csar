interface DefaultMethodSample {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }

    default <T> T firstOrNull(List<T> list) {
        return list.size() > 0 ? list.get(0) : null;
    }
}
