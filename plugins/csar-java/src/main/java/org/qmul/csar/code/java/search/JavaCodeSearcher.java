package org.qmul.csar.code.java.search;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.RefactorTarget;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.TargetDescriptor;
import org.qmul.csar.result.Result;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * A multi-threaded project code searcher.
 */
public class JavaCodeSearcher extends MultiThreadedTaskProcessor implements ProjectCodeSearcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeSearcher.class);
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final List<Result> results = Collections.synchronizedList(new ArrayList<>());
    private final List<RefactorTarget> refactorTargets = Collections.synchronizedList(new ArrayList<>());
    private ConcurrentIterator<Map.Entry<Path, Statement>> it;
    private CsarQuery query;

    /**
     * Creates a new {@link JavaCodeSearcher} with the argument thread count.
     *
     * @param threadCount the amount of threads to use
     */
    public JavaCodeSearcher(int threadCount) {
        super(threadCount, "csar-search");
        setRunnable(new Task());
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
     */
    public List<Result> results() {
        LOGGER.info("Starting...");
        LOGGER.trace("Domain (FromQuery): {}", query.getFromTarget());

        // Execute and return results
        long startTime = System.currentTimeMillis();
        run();

        // Log completion message
        LOGGER.debug("Time taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
        return results;
    }

    @Override
    public List<RefactorTarget> refactorTargets() {
        return refactorTargets;
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            Map.Entry<Path, Statement> entry;
            Path file = null;
            Statement statement;

            try {
                while (!Thread.currentThread().isInterrupted() && it.hasNext()) {
                    // Get the next entry
                    try {
                        entry = it.next();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    file = entry.getKey();
                    statement = entry.getValue();
                    String fileName = file.getFileName().toString();
                    LOGGER.trace("Searching {}", fileName);

                    try {
                        // Search file and store the results
                        TargetDescriptor searchTarget = query.getSearchTarget();
                        Searcher.Result result = SearcherFactory.create(searchTarget).search(query, file, statement);
                        results.addAll(result.getResults());
                        refactorTargets.addAll(result.getRefactorTargets());
                    } catch (RuntimeException ex) {
                        Path finalFile = file;
                        errorListeners.forEach(l -> l.errorSearching(finalFile, ex));
                    }
                }
            } catch (Exception ex) {
                Path finalFile = file;
                errorListeners.forEach(l -> l.fatalErrorSearching(finalFile, ex));
                terminate();
            }
        }
    }
}
