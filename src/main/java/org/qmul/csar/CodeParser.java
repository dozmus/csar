package org.qmul.csar;

import org.qmul.csar.code.CodeTreeParserFactory;
import org.qmul.csar.code.Node;
import org.qmul.csar.code.NodeHelper;
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
        Objects.requireNonNull(it);
        this.it = it;
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
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                String fileName = "";

                try {
                    while (hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next file
                        Path file = next();
                        fileName = file.getFileName().toString();
                        Node root;

                        try {
                            root = CodeTreeParserFactory.parse(file);
                        } catch (IOException ex) {
                            LOGGER.error("Failed to read file {} because {}", fileName, ex.getMessage());

                            if (LOGGER.isTraceEnabled()) {
                                ex.printStackTrace();
                            }
                            continue;
                        } catch (RuntimeException ex) {
                            LOGGER.error("Failed to parsed file {} because {}", fileName, ex.getMessage());

                            if (LOGGER.isTraceEnabled()) {
                                ex.printStackTrace();
                            }
                            continue;
                        }

                        // Print code tree
                        if (LOGGER.isTraceEnabled() && root != null) {
                            LOGGER.trace("Tree for {}:\r\n{}", fileName, NodeHelper.toStringRecursively(root));
                        }

                        // TODO finish?
                        LOGGER.info("Parsed {}", fileName);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Parsing terminated {} because {}", fileName, ex.getMessage());

                    if (LOGGER.isTraceEnabled()) {
                        ex.printStackTrace();
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
