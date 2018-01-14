public class Sample4 {

    void work() {
        interface Runnable {
            void run();
        }

        class A implements Runnable {
            public void run() {
                int x = 30;
                System.out.println(x);
            }
        }
        A worker = new A();
        worker.run();
    }

    void work2() throws IOException { // Test throws method signature
    }
}
