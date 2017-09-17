public enum Season {
    WINTER("Cold"), SPRING("Warmer"), SUMMER("Hot"), AUTUMN("Cooler");

    Season(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
