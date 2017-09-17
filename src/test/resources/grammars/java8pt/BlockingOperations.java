@interface BlockingOperations {
    boolean fileSystemOperations();
    boolean networkOperations() default false;
}
