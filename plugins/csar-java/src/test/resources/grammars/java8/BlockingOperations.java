@interface BlockingOperations {
    @BlockingOperations2
    boolean fileSystemOperations();
    boolean networkOperations() default false;
}
