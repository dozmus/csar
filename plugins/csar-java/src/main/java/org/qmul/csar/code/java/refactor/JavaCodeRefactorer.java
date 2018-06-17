package org.qmul.csar.code.java.refactor;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.code.refactor.RefactorTarget;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * A multi-threaded project code refactorer.
 */
public class JavaCodeRefactorer extends MultiThreadedTaskProcessor implements ProjectCodeRefactorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeRefactorer.class);
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final List<Result> results = Collections.synchronizedList(new ArrayList<>());
    private final boolean writeToFiles;
    private RefactorDescriptor refactorDescriptor;
    private List<RefactorTarget> refactorTargets;
    private ConcurrentIterator<Map.Entry<Path, List<RefactorChange>>> it;

    /**
     * Constructs a new {@link JavaCodeRefactorer} with the given arguments.
     *
     * @param threadCount the amount of threads to use
     * @param writeToFiles if the refactor changes should be written to the files
     */
    public JavaCodeRefactorer(int threadCount, boolean writeToFiles) {
        super(threadCount, "csar-refactor");
        this.writeToFiles = writeToFiles;
        setRunnable(new Task());
    }

    @Override
    public List<Result> results() {
        LOGGER.info("Starting...");

        // Group search results by file name, to allow multi-threading without resource races
        it = new ConcurrentIterator<>(groupedRefactorChanges().entrySet().iterator());

        // Execute and return results
        long startTime = System.currentTimeMillis();
        run();

        // Log completion message
        LOGGER.debug("Time taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
        return results;
    }

    /**
     * Returns the refactor targets as {@link RefactorChange} instances grouped by file name.
     */
    private Map<Path, List<RefactorChange>> groupedRefactorChanges() {
        Map<Path, List<RefactorChange>> results = new HashMap<>();

        for (RefactorTarget target : refactorTargets) {
            Path path = pathOfRefactorTarget(target);
            RefactorChange change = changeFromRefactorTarget(target);

            if (results.containsKey(path)) {
                results.get(path).add(change);
            } else {
                List<RefactorChange> list = new ArrayList<>();
                list.add(change);
                results.put(path, list);
            }
        }
        return results;
    }

    /**
     * Returns the path this refactor target represents.
     */
    private Path pathOfRefactorTarget(RefactorTarget target) {
        if (target instanceof RefactorTarget.Expression) {
            return ((MethodCallExpression)((RefactorTarget.Expression) target).getExpression()).getPath();
        } else if (target instanceof RefactorTarget.Statement) {
            return ((MethodStatement)((RefactorTarget.Statement) target).getStatement()).getPath();
        }
        throw new RuntimeException("invalid refactor target type: " + target.getClass());
    }

    private RefactorChange changeFromRefactorTarget(RefactorTarget target) {
        if (target instanceof RefactorTarget.Expression) {
            MethodCallExpression e = ((MethodCallExpression)((RefactorTarget.Expression) target).getExpression());

            if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                return new MethodCallExpressionIdentifierRefactorChange(e);
            } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                return new MethodCallExpressionChangeParametersRefactorChange(e);
            }
        } else if (target instanceof RefactorTarget.Statement) {
            MethodStatement m = ((MethodStatement)((RefactorTarget.Statement) target).getStatement());

            if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                return new MethodStatementIdentifierRefactorChange(m);
            } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                return new MethodStatementChangeParametersRefactorChange(m);
            }
        }
        throw new RuntimeException("invalid refactor target type: " + target.getClass());
    }

    @Override
    public void setRefactorDescriptor(RefactorDescriptor descriptor) {
        this.refactorDescriptor = Objects.requireNonNull(descriptor);
    }

    @Override
    public void setRefactorTargets(List<RefactorTarget> refactorTargets) {
        this.refactorTargets = Objects.requireNonNull(refactorTargets);
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
            Map.Entry<Path, List<RefactorChange>> entry;
            Path file = null;
            List<RefactorChange> searchResults;

            try {
                while (!Thread.currentThread().isInterrupted() && it.hasNext()) {
                    // Get the next entry
                    try {
                        entry = it.next();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    file = entry.getKey();
                    searchResults = entry.getValue();
                    String fileName = file.getFileName().toString();
                    LOGGER.trace("Refactoring {}", fileName);

                    try {
                        // Refactor file and store the results
                        List<Result> tmpResults = RefactorerFactory.create(refactorDescriptor, writeToFiles)
                                .refactor(file, searchResults);
                        results.addAll(tmpResults);
                    } catch (RuntimeException ex) {
                        Path finalFile = file;
                        errorListeners.forEach(l -> l.errorRefactoring(finalFile, ex));
                    }
                }
            } catch (Exception ex) {
                Path finalFile = file;
                errorListeners.forEach(l -> l.fatalErrorRefactoring(finalFile, ex));
                terminate();
            }
        }
    }
}
