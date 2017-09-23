package org.qmul.csar;

import org.qmul.csar.code.CodeParserFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded code parser.
 */
public final class CodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeParser.class);
    private final ExecutorService executor;
    private final Iterator<Path> it;
    private final int threadCount;
    private final CountDownLatch finishedLatch;
    private boolean running = false;

    public CodeParser(Iterator<Path> it) {
        this(it, 1);
    }

    public CodeParser(Iterator<Path> it, int threadCount) {
        if (threadCount <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        this.it = Objects.requireNonNull(it);
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("csar-parse-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
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
        LOGGER.info("Starting...");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                String fileName = "";

                try {
                    while (hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next file
                        Path file = next();
                        fileName = file.getFileName().toString();
                        Statement root;

                        try {
                            root = CodeParserFactory.parse(file);
                        } catch (IOException | RuntimeException ex) {
                            String phrase = (ex instanceof IOException) ? "read" : "parse";
                            LOGGER.error("Failed to {} file {} because {}", phrase, fileName, ex.getMessage());

                            if (LOGGER.isTraceEnabled()) {
                                ex.printStackTrace();
                            }
                            continue;
                        }

                        // Print code tree
                        if (LOGGER.isTraceEnabled() && root != null) {
                            LOGGER.trace("Tree for {}:\r\n{}", fileName, root.toPseudoCode());
                        }

                        // TODO finish?
                        LOGGER.trace("Parsed {}", fileName);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Parsing terminated {} because {}", fileName, ex.getMessage());

                    if (LOGGER.isTraceEnabled()) {
                        ex.printStackTrace();
                    }
                    // TODO this is a fatal error, exit the program
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
     * @throws InterruptedException if the current thread is interrupted while waiting
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
