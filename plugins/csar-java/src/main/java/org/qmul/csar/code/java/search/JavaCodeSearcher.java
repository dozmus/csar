package org.qmul.csar.code.java.search;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.RefactorTarget;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.SearchType;
import org.qmul.csar.query.TargetDescriptor;
import org.qmul.csar.result.Result;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Returns search matches for method usages.
     *
     * @param targetDescriptor the target descriptor to search for
     * @param statement the parsed contents of the file being searched
     * @return returns search matches for method definitions
     */
    private InternalResult searchMethodUsage(TargetDescriptor targetDescriptor, Statement statement) {
        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visitStatement(statement);

        // Aggregate and return results
        List<Result> results = new ArrayList<>();
        List<RefactorTarget> refactorTargets = new ArrayList<>();

        for (Statement st : visitor.getResults()) {
            MethodStatement method = (MethodStatement)st;

            // Create Results
            List<Result> tmpResults = method.getMethodUsages()
                    .stream()
                    .filter(expr -> {
                        if (query.getFromTarget().size() == 0)
                            return true;

                        // From Query
                        String fileNameWithoutExt = StringUtils.fileNameWithoutExtension(expr.getPath());

                        for (String fromDomain : query.getFromTarget()) {
                            if (fromDomain.equals(fileNameWithoutExt)) {
                                LOGGER.trace("Accepted: {}", fileNameWithoutExt);
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(expr -> new Result(expr.getPath(), expr.getLineNumber(), expr.toPseudoCode())) // create Result
                    .collect(Collectors.toList());
            results.addAll(tmpResults);

            // Create RefactorTargets (these are not restricted by search domain, or the output would be incorrect)
            List<RefactorTarget> tmpRefactorTargets = method.getMethodUsages().stream()
                    .map(RefactorTarget.Expression::new)
                    .collect(Collectors.toList());
            refactorTargets.add(new RefactorTarget.Statement(method));
            refactorTargets.addAll(tmpRefactorTargets);
        }
        return new InternalResult(results, refactorTargets);
    }

    /**
     * Returns search matches for method definitions.
     *
     * @param targetDescriptor the target descriptor to search for
     * @param path the file being searched
     * @param statement the parsed contents of the file being searched
     * @return returns search matches for method definitions
     */
    private InternalResult searchMethodDefinition(TargetDescriptor targetDescriptor, Path path, Statement statement) {
        // From Query
        if (query.getFromTarget().size() > 0) {
            String fileNameWithoutExt = StringUtils.fileNameWithoutExtension(path);
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
                return new InternalResult(new ArrayList<>(), new ArrayList<>());
            }
        }

        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visitStatement(statement);

        // Aggregate and return results
        List<Result> results = visitor.getResults().stream()
                .map(s -> new Result(path, ((MethodStatement)s).getLineNumber(), s.toPseudoCode()))
                .collect(Collectors.toList());

        // Create RefactorTargets (these are not restricted by search domain, or the output would be incorrect)
        List<RefactorTarget> refactorTargets = new ArrayList<>();
        visitor.getResults().stream()
                .map(s -> (MethodStatement)s)
                .forEach(m -> {
                    refactorTargets.add(new RefactorTarget.Statement(m));
                    m.getMethodUsages().forEach(mce -> refactorTargets.add(new RefactorTarget.Expression(mce)));
                });
        return new InternalResult(results, refactorTargets);
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
                        Descriptor targetDescriptor = searchTarget.getDescriptor();

                        if (targetDescriptor instanceof MethodDescriptor) {
                            InternalResult internalResult;

                            // Get results for file
                            if (query.getSearchTarget().getSearchType().get() == SearchType.DEF) {
                                internalResult = searchMethodDefinition(searchTarget, file, statement);
                            } else {
                                internalResult = searchMethodUsage(searchTarget, statement);
                            }

                            // Add to overall results
                            results.addAll(internalResult.results);
                            refactorTargets.addAll(internalResult.refactorChanges);
                        } else {
                            throw new UnsupportedOperationException("unsupported search target: "
                                    + targetDescriptor.getClass().getName());
                        }
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

    private static final class InternalResult {

        private final List<Result> results;
        private final List<RefactorTarget> refactorChanges;

        InternalResult(List<Result> results, List<RefactorTarget> refactorChanges) {
            this.results = results;
            this.refactorChanges = refactorChanges;
        }
    }
}
