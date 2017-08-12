package org.qmul;

import org.qmul.io.ProjectCodeIterator;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded code file processor.
 */
public final class CodeProcessor {

    private final ExecutorService executor;
    private final ProjectCodeIterator it;
    private final int threads;

    public CodeProcessor(ProjectCodeIterator it) {
        this(it, 1);
    }

    public CodeProcessor(ProjectCodeIterator it, int threads) {
        this.it = it;
        this.threads = threads;
        if (threads <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        executor = Executors.newFixedThreadPool(threads);
    }

    public void run() {
        if (!it.hasNext()) {
            return;
        }

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        Path file = next();
                        // TODO parse file
                        System.out.println(Thread.currentThread().getName() + ": parsed "
                                + file.getFileName().toString());
                    }
                } catch (IllegalStateException ex) {
                    System.out.println(Thread.currentThread().getName() + ": done");
                }
            });
        }
    }

    private synchronized Path next() throws IllegalStateException {
        if (it.hasNext()) {
            return it.next();
        }
        throw new IllegalStateException("ran out of code files");
    }
}
