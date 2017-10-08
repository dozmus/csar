package org.qmul.csar.code.parse;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded project-wide code parser.
 */
public class ProjectCodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCodeParser.class);
    private final ExecutorService executor;
    private final CountDownLatch finishedLatch;
    private final int threadCount;
    private final Iterator<Path> it;
    private boolean errorOccurred = false;
    private boolean running = false;

    /**
     * Creates a new {@link ProjectCodeParser} with the argument iterator and a <tt>threadCount</tt> of <tt>1</tt>.
     * @param it the {@link Path} iterator whose contents to parse
     */
    public ProjectCodeParser(Iterator<Path> it) {
        this(it, 1);
    }

    /**
     * Creates a new {@link ProjectCodeParser} with the arguments.
     * @param it the {@link Path} iterator whose contents to parse
     * @param threadCount the amount of threads to use
     * @throws IllegalArgumentException if <tt>threadCount</tt> is less than or equal to <tt>0</tt>
     */
    public ProjectCodeParser(Iterator<Path> it, int threadCount) {
        this.it = Objects.requireNonNull(it);

        if (threadCount <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("csar-parse-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
    }

    /**
     * Submits tasks to the underlying thread pool to begin processing code files, this is a blocking operation, and
     * upon completion returns a map with parsed files as keys, and the output statements as values.
     * If a fatal error occurs then all parsing is gracefully terminated, then {@link #errorOccurred()} is set to
     * <tt>true</tt> and returns partial results.
     * If {@link #it} contains no files then an empty map is returned.
     * This should only be called once per instance of this class.
     * @return a map with parsed files as keys, and the output statements as values.
     * @throws IllegalStateException if it has already been called on this instance, or if it is currently running
     */
    public Map<Path, Statement> results() {
        // Check if ready to run
        if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (running) {
            throw new IllegalStateException("already running");
        } else if (!it.hasNext()) {
            return new HashMap<>();
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
                        LOGGER.debug("Parsing {}", fileName);

                        // Parse file
                        try {
                            root = CodeParserFactory.create(file).parse(file);
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
                        LOGGER.debug("Parsed {}", fileName);
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

    /**
     * Returns <tt>true</tt> if an error occurred within {@link #results()} which did not result in an exception being
     * thrown.
     * @return <tt>true</tt> if an error occurred within {@link #results()}
     */
    public boolean errorOccurred() {
        return errorOccurred;
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
