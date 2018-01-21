package org.qmul.csar.code.java.search;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.SearchType;
import org.qmul.csar.query.TargetDescriptor;
import org.qmul.csar.result.Result;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.NamedThreadFactory;
import org.qmul.csar.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A multi-threaded project code searcher.
 */
public class JavaCodeSearcher implements ProjectCodeSearcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeSearcher.class);
    private final CountDownLatch finishedLatch;
    private final int threadCount;
    private final ExecutorService executor;
    private ConcurrentIterator<Map.Entry<Path, Statement>> it;
    private List<CsarErrorListener> errorListeners = new ArrayList<>();
    private CsarQuery query;
    private boolean errorOccurred = false;
    private boolean running = false;

    /**
     * Creates a new {@link JavaCodeSearcher} with the argument thread count.
     * @param threadCount the amount of threads to use
     */
    public JavaCodeSearcher(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("csar-search-%d"));
        this.finishedLatch = new CountDownLatch(threadCount);
    }

    /**
     * Sets the csar query to search for.
     *
     * @param query the search query
     */
    public void setCsarQuery(CsarQuery query) {
        this.query = Objects.requireNonNull(query);
    }

    /**
     * Sets the iterator whose contents to search
     *
     * @param it the iterator whose contents to search
     */
    public void setIterator(Iterator<Map.Entry<Path, Statement>> it) {
        this.it = new ConcurrentIterator<>(Objects.requireNonNull(it));
    }

    /**
     * Returns a list containing the results from {@link #it} which matched the search query.
     * If {@link #it} contains no files then an empty list is returned.
     * @return returns the results.
     * @throws IllegalStateException if it has already been called on this instance, or if it is currently running.
     */
    public List<Result> results() {
        // Check if ready to run
        if (runningCount() == 0) {
            throw new IllegalStateException("already finished running");
        } else if (running) {
            throw new IllegalStateException("already running");
        } else if (!it.hasNext()) {
            return new ArrayList<>();
        }
        running = true;

        // Submit tasks
        final List<Result> results = Collections.synchronizedList(new ArrayList<>());
        LOGGER.info("Starting...");
        LOGGER.trace("Domain (FromQuery): {}", query.getFromTarget());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Map.Entry<Path, Statement> entry;
                Path file = null;
                Statement statement;

                try {
                    while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                        // Get the next entry
                        entry = it.next();
                        file = entry.getKey();
                        statement = entry.getValue();
                        String fileName = file.getFileName().toString();
                        LOGGER.trace("Searching {}", fileName);

                        try {
                            // Search file and store the results
                            TargetDescriptor searchTarget = query.getSearchTarget();
                            Descriptor targetDescriptor = searchTarget.getDescriptor();

                            if (targetDescriptor instanceof MethodDescriptor) {
                                if (query.getSearchTarget().getSearchType().get() == SearchType.DEF) {
                                    results.addAll(methodDefinitionSearch(searchTarget, file, statement));
                                } else {
                                    results.addAll(methodUsageSearch(searchTarget, statement));
                                }
                            } else {
                                throw new UnsupportedOperationException("unsupported search target: "
                                        + targetDescriptor.getClass().getName());
                            }
                        } catch (RuntimeException ex) {
                            Path finalFile = file;
                            errorListeners.forEach(l -> l.errorSearching(finalFile, ex));
                        }
                        LOGGER.trace("Searched {}", fileName);
                    }
                } catch (Exception ex) {
                    Path finalFile = file;
                    errorListeners.forEach(l -> l.fatalErrorSearching(finalFile, ex));
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
        LOGGER.info("Finished");

        synchronized (this) {
            running = false;
        }
        return results;
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

    /**
     * Returns search matches for method usages.
     * @param targetDescriptor the target descriptor to search for
     * @param statement the parsed contents of the file being searched
     * @return returns search matches for method definitions
     */
    private List<Result> methodUsageSearch(TargetDescriptor targetDescriptor, Statement statement) {
        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visitStatement(statement);

        // Aggregate and return results
        List<Result> results = new ArrayList<>();

        for (Statement st : visitor.getResults()) {
            MethodStatement method = (MethodStatement)st;
            List<Result> tmpResults = method.getMethodUsages()
                    .stream()
                    .filter(expr -> {
                        if (query.getFromTarget().size() == 0)
                            return true;

                        // From Query
                        String fileNameWithoutExt = StringUtils.getFileNameWithoutExtension(expr.getPath());

                        for (String fromDomain : query.getFromTarget()) {
                            if (fromDomain.equals(fileNameWithoutExt)) {
                                LOGGER.trace("Accepted: {}", fileNameWithoutExt);
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(expr -> new Result(expr.getPath(), expr.getLineNumber(), expr.toPseudoCode()))
                    .collect(Collectors.toList());
            results.addAll(tmpResults);
        }
        return results;
    }

    /**
     * Returns search matches for method definitions.
     * @param targetDescriptor the target descriptor to search for
     * @param path the file being searched
     * @param statement the parsed contents of the file being searched
     * @return returns search matches for method definitions
     */
    private List<Result> methodDefinitionSearch(TargetDescriptor targetDescriptor, Path path, Statement statement) {
        // From Query
        if (query.getFromTarget().size() > 0) {
            String fileNameWithoutExt = StringUtils.getFileNameWithoutExtension(path);
            boolean valid = false;

            for (String fromDomain : query.getFromTarget()) {
                if (fromDomain.equals(fileNameWithoutExt)) {
                    valid = true;
                    LOGGER.trace("Accepted: {}", fileNameWithoutExt);
                    break;
                }
            }

            if (!valid) {
                LOGGER.trace("Skipped {}", path);
                return new ArrayList<>();
            }
        }

        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visitStatement(statement);

        // Aggregate and return results
        return visitor.getResults()
                .stream().map(s -> new Result(path, ((MethodStatement)s).getLineNumber(), s.toPseudoCode()))
                .collect(Collectors.toList());
    }
}
