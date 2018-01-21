package org.qmul.csar.code.parse;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded project-wide code parser.
 */
public class DefaultProjectCodeParser implements ProjectCodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProjectCodeParser.class);
    private final ExecutorService executor;
    private final CountDownLatch finishedLatch;
    private final int threadCount;
    private final ConcurrentIterator<Path> it;
    private boolean errorOccurred = false;
    private boolean running = false;
    private List<CsarErrorListener> errorListeners = new ArrayList<>();
    private CodeParserFactory factory;

    /**
     * Creates a new {@link DefaultProjectCodeParser} with the argument factory, iterator and a <tt>threadCount</tt> of
     * <tt>1</tt>.
     *
     * @param factory the {@link CodeParserFactory} to use to create parsers
     * @param it the {@link Path} iterator whose contents to parse
     */
    public DefaultProjectCodeParser(CodeParserFactory factory, Iterator<Path> it) {
        this(factory, it, 1);
    }

    /**
     * Creates a new {@link DefaultProjectCodeParser}.
     *
     * @param factory the {@link CodeParserFactory} to use to create parsers
     * @param it the {@link Path} iterator whose contents to parse
     * @param threadCount the amount of threads to use
     * @throws IllegalArgumentException if <tt>threadCount</tt> is less than or equal to <tt>0</tt>
     * @throws NullPointerException if it or factory is <tt>null</tt>
     */
    public DefaultProjectCodeParser(CodeParserFactory factory, Iterator<Path> it, int threadCount) {
        this.it = new ConcurrentIterator<>(Objects.requireNonNull(it));
        this.factory = Objects.requireNonNull(factory);
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
        long startTime = System.currentTimeMillis();
        TotalFileSizes totalFileSizes = new TotalFileSizes();
        LOGGER.info("Starting...");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Path file = null;
                Statement root;

                try {
                    while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next file
                        file = it.next();
                        String fileName = file.getFileName().toString();
                        LOGGER.trace("Parsing {}", fileName);

                        try {
                            // Parse file and put in the map
                            root = factory.create(file).parse(file);
                            map.put(file, root);

                            // Update statistics
                            synchronized (totalFileSizes) {
                                totalFileSizes.add(Files.size(file));
                            }

                            // Print code tree
                            LOGGER.trace("Tree for {}:\r\n{}", fileName, root.toPseudoCode());
                        } catch (IOException | RuntimeException ex) {
                            Path finalFile = file;
                            errorListeners.forEach(l -> l.errorParsing(finalFile, ex));
                        }
                        LOGGER.trace("Parsed {}", fileName);
                    }
                } catch (Exception ex) {
                    Path finalFile = file;
                    errorListeners.forEach(l -> l.fatalErrorParsing(finalFile, ex));
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
            String msg = "Error waiting for termination because the current thread was interrupted - terminating tasks.";
            LOGGER.error(msg);
            executor.shutdownNow();
        }

        // Log completion message
        LOGGER.debug("Parsed {}kb of code in {}ms", totalFileSizes.sizeKb,
                (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");

        synchronized (this) {
            running = false;
        }
        return map;
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    /**
     * Returns <tt>true</tt> if an error occurred within {@link #results()} which did not result in an exception being
     * thrown.
     * @return <tt>true</tt> if an error occurred within {@link #results()}
     */
    public boolean errorOccurred() {
        return errorOccurred;
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

    private static final class TotalFileSizes {

        private long sizeKb = 0;

        public void add(long sizeBytes) {
            sizeKb += Math.round((double)sizeBytes / 1000D);
        }
    }
}
