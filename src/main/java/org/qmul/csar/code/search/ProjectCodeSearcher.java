package org.qmul.csar.code.search;

import org.qmul.csar.code.PathProcessorErrorListener;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.SearchType;
import org.qmul.csar.query.TargetDescriptor;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded project code searcher.
 */
public class ProjectCodeSearcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCodeSearcher.class);
    private final int threadCount;
    private ExecutorService executor;
    private CountDownLatch finishedLatch;
    private ConcurrentIterator<Map.Entry<Path, Statement>> it;
    private boolean errorOccurred = false;
    private boolean running = false;
    private PathProcessorErrorListener errorListener;
    private CsarQuery query;
    private boolean initialized = false;

    /**
     * Creates a new {@link ProjectCodeSearcher} with the argument thread count.
     * @param threadCount the amount of threads to use
     */
    public ProjectCodeSearcher(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Initializes the state of the searcher.
     *
     * @param query the search query
     * @param it the iterator whose contents to search
     */
    public void init(CsarQuery query, Iterator<Map.Entry<Path, Statement>> it) {
        if (initialized)
            throw new IllegalStateException("cannot init twice");
        initialized = true;
        this.query = query;
        this.it = new ConcurrentIterator<>(Objects.requireNonNull(it));
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("csar-search-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
    }

    /**
     * Returns a list containing the statements from {@link #it} which matched the search query.
     * If {@link #it} contains no files then an empty list is returned.
     * @return returns the matches.
     * @throws IllegalStateException if it has already been called on this instance, or if it is currently running,
     * or if it is not initialized ({@link #initialized} is <tt>false</tt>).
     */
    public List<Statement> results() {
        // Check if ready to run
        if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (running) {
            throw new IllegalStateException("already running");
        } else if (!initialized) {
            throw new IllegalStateException("not initialized");
        } else if (!it.hasNext()) {
            return new ArrayList<>();
        }
        running = true;

        // Submit tasks
        final List<Statement> results = Collections.synchronizedList(new ArrayList<>());
        LOGGER.info("Starting...");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Map.Entry<Path, Statement> entry;
                Path file = null;
                Statement statement = null;

                try {
                    while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next entry
                        entry = it.next();
                        file = entry.getKey();
                        statement = entry.getValue();
                        String fileName = file.getFileName().toString();
                        LOGGER.debug("Searching {}", fileName);

                        try {
                            // Search file and store the results
                            TargetDescriptor searchTarget = query.getSearchTarget();
                            Descriptor targetDescriptor = searchTarget.getDescriptor();

                            if (targetDescriptor instanceof MethodDescriptor
                                    && query.getSearchTarget().getSearchType().get() == SearchType.DEF) {
                                results.addAll(methodDefinitionSearch(searchTarget, file, statement));
                            } else {
                                throw new UnsupportedOperationException("unsupported search target: "
                                        + targetDescriptor.getClass().getName());
                            }
                        } catch (RuntimeException ex) {
                            if (errorListener != null) {
                                errorListener.reportRecoverableError(file, ex);
                            }
                        }
                        LOGGER.debug("Searched {}", fileName);
                    }
                } catch (Exception ex) {
                    if (errorListener != null) {
                        errorListener.reportUnrecoverableError(file, ex);
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
        return results;
    }

    public PathProcessorErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(PathProcessorErrorListener errorListener) {
        this.errorListener = errorListener;
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

    /**
     * Returns search matches for method definitions.
     * @param targetDescriptor the target descriptor to search for
     * @param path the file being searched
     * @param statement the parsed contents of the file being searched
     * @return returns search matches for method definitions
     */
    private List<Statement> methodDefinitionSearch(TargetDescriptor targetDescriptor, Path path, Statement statement) {
        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visit(statement);

        // TODO: containsQuery
        // TODO: fromTarget

        // Add results
        return visitor.getResults();
    }
}
