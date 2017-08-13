package org.qmul.csar;

import org.qmul.csar.io.ProjectCodeIterator;

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
    private int activeThreads = 0;

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

    /**
     * Submits tasks to the underlying thread pool to begin processing code files.
     */
    public void run() {
        // Check if ready to run
        if (!it.hasNext()) {
            return;
        } else if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        // Submit tasks
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                addActiveThread(1);

                while (hasNext()) {
                    Path file = next();
                    // TODO parse file
                    System.out.println(Thread.currentThread().getName() + ": parsed "
                            + file.getFileName().toString());
                }
                System.out.println(Thread.currentThread().getName() + ": done");
                addActiveThread(-1);
            });
        }
    }

    /**
     * Thread-safe.
     * @return {@link #it#hasNext()}
     */
    private boolean hasNext() {
        synchronized (it) {
            return it.hasNext();
        }
    }

    /**
     * Thread-safe.
     * @return {@link #it#next()}
     */
    private Path next() {
        synchronized (it) {
            if (it.hasNext()) {
                return it.next();
            }
        }
        throw new IllegalStateException("ran out of code files");
    }

    /**
     * Thread-safe.
     */
    public synchronized boolean isRunning() {
        return activeThreads > 0;
    }

    /**
     * Thread-safe.
     */
    private synchronized void addActiveThread(int n) {
        activeThreads += n;
    }
}
