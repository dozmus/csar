class Dummy {
    @BlockingOperations(/*mandatory*/ fileSystemOperations = true,
    /*optional*/ networkOperations = true)
    void openOutputStream() { //Annotated method
    }

    @Unused // Shorthand for @Unused()
    void travelToJupiter() {
    }
}
