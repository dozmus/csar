public enum Currency {
    USD(1.10),
    GBP(1);

    private final double value;

    private Currency(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
