class MethodSample {
    int bar(int a, int b) {
        return (a*2) + b;
    }

    /* Overloaded method with the same name but different set of arguments */
    int bar(int a) {
        return a*2;
    }

    void openStream() throws IOException, myException { // Indicates that IOException may be thrown
    }

    // Varargs
    void printReport(String header, int... numbers) { //numbers represents varargs
        System.out.println(header);
        for (int num : numbers) {
            System.out.println(num);
        }
    }
}
