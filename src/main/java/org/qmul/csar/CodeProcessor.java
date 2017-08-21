package org.qmul.csar;

import org.qmul.csar.io.ProjectCodeIterator;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded code file processor.
 */
public final class CodeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeProcessor.class);
    private final ExecutorService executor;
    private final ProjectCodeIterator it;
    private final int threads;
    private final CountDownLatch finishedLatch;
    private boolean running = false;

    public CodeProcessor(ProjectCodeIterator it) {
        this(it, 1);
    }

    public CodeProcessor(ProjectCodeIterator it, int threads) {
        if (threads <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        this.it = it;
        this.threads = threads;
        this.executor = Executors.newFixedThreadPool(threads, new NamedThreadFactory("csar-%d"));
        this.finishedLatch = new CountDownLatch(threads);
    }

    /**
     * Submits tasks to the underlying thread pool to begin processing code files, this is non-blocking.
     * This should only be called once per instance of this class.
     */
    public void run() {
        // Check if ready to run
        if (!it.hasNext()) {
            throw new IllegalStateException("no code files available");
        } else if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (isRunning()) {
            throw new IllegalStateException("already running");
        }
        running = true;

        // Submit tasks
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    while (hasNext() && !Thread.currentThread().isInterrupted()) {
                        Path file = next();
                        // TODO parse file
                        LOGGER.info("Parsed {}", file.getFileName().toString());
                    }
                } finally {
                    LOGGER.info("Finished");
                    countDown();
                    updateRunning();
                }
            });
        }
        executor.shutdown();
    }

    /**
     * Executes {@link #run()} and blocks until finished, or an {@link InterruptedException} is thrown.
     */
    public void runAndWait() throws InterruptedException {
        // Run
        run();

        // Wait
        finishedLatch.await();
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

    public synchronized boolean isRunning() {
        return running;
    }

    private synchronized void updateRunning() {
        running = (runningCount() > 0);
    }

    private long runningCount() {
        synchronized (finishedLatch) {
            return finishedLatch.getCount();
        }
    }

    private void countDown() {
        synchronized (finishedLatch) {
            finishedLatch.countDown();
        }
    }
}
