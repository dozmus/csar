package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded project code parser.
 */
public final class ProjectCodeParser extends AbstractProjectCodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCodeParser.class);
    private final ExecutorService executor;
    private final CountDownLatch finishedLatch;
    private final int threadCount;
    private boolean errorOccurred = false;
    private boolean running = false;

    public ProjectCodeParser(Iterator<Path> it) {
        this(it, 1);
    }

    public ProjectCodeParser(Iterator<Path> it, int threadCount) {
        super(it);

        if (threadCount <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("csar-parse-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
    }

    /**
     * Submits tasks to the underlying thread pool to begin processing code files, this is blocking, and will return
     * once finished or if interrupted, with partial results.
     * If a fatal error occurs all parsing is gracefully terminated, and then {@link #errorOccurred()} is set to
     * <tt>true</tt>.
     * This should only be called once per instance of this class.
     * @return a map with parsed files as keys, and the output statements as values.
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Override
    public Map<Path, Statement> results() {
        // Check if ready to run
        if (!getIt().hasNext()) {
            throw new IllegalStateException("no code files available");
        } else if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (running) {
            throw new IllegalStateException("already running");
        }
        running = true;

        // Submit tasks
        final ConcurrentHashMap<Path, Statement> map = new ConcurrentHashMap<>();
        LOGGER.info("Starting...");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                String fileName = "";
                Statement root;

                try {
                    while (hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next file
                        Path file = next();
                        fileName = file.getFileName().toString();
                        LOGGER.trace("Parsing {}", fileName);

                        // Parse file
                        try {
                            root = CodeParserFactory.parse(file);
                            map.put(file, root);
                        } catch (IOException | RuntimeException ex) {
                            String phrase = (ex instanceof IOException) ? "read" : "parse";
                            LOGGER.error("Failed to {} file {} because {}", phrase, fileName, ex.getMessage());

                            if (LOGGER.isTraceEnabled()) {
                                ex.printStackTrace();
                            }
                            continue;
                        }

                        // Print code tree
                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Tree for {}:\r\n{}", fileName, root.toPseudoCode());
                        }
                        LOGGER.trace("Parsed {}", fileName);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Parsing terminated {} because {}", fileName, ex.getMessage());

                    if (LOGGER.isTraceEnabled()) {
                        ex.printStackTrace();
                    }
                    setErrorOccurred();
                    executor.shutdownNow();
                } finally {
                    LOGGER.trace("Finished");
                    countDown();
                    updateRunning();
                }
            });
        }

        // Wait for termination
        executor.shutdown();

        try {
            finishedLatch.await();
        } catch (InterruptedException e) {
            String msg = "Error waiting for termination because the current thread was interrupted- terminating tasks.";
            LOGGER.error(msg);
            executor.shutdownNow();
        }
        LOGGER.info("Finished");

        synchronized (this) {
            running = false;
        }
        return map;
    }

    @Override
    public boolean errorOccurred() {
        return errorOccurred;
    }

    /**
     * Thread-safe.
     * @return {@link #it#hasNext()}
     */
    private boolean hasNext() {
        synchronized (getIt()) {
            return getIt().hasNext();
        }
    }

    /**
     * Thread-safe.
     * @return {@link #it#next()}
     */
    private Path next() {
        synchronized (getIt()) {
            if (getIt().hasNext()) {
                return getIt().next();
            }
        }
        throw new IllegalStateException("ran out of code files");
    }

    private synchronized void updateRunning() {
        running = (runningCount() > 0);
    }

    private synchronized void setErrorOccurred() {
        synchronized (this) {
            errorOccurred = true;
        }
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
