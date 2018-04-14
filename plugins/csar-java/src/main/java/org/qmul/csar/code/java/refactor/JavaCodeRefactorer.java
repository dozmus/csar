package org.qmul.csar.code.java.refactor;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.ProjectCodeRefactorer;
import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.code.RefactorTarget;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.result.Result;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A multi-threaded project code refactorer.
 */
public class JavaCodeRefactorer extends MultiThreadedTaskProcessor implements ProjectCodeRefactorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeRefactorer.class);
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final List<Result> results = Collections.synchronizedList(new ArrayList<>());
    private RefactorDescriptor refactorDescriptor;
    private List<RefactorTarget> searchResults;
    private ConcurrentIterator<Map.Entry<Path, List<RefactorChange>>> it;

    /**
     * Constructs a new {@link JavaCodeRefactorer} with the given arguments.
     *
     * @param threadCount the amount of threads to use
     */
    public JavaCodeRefactorer(int threadCount) {
        super(threadCount, "csar-refactor");
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

        for (RefactorTarget target : searchResults) {
            Path path = pathFromRefactorTarget(target);
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

    private Path pathFromRefactorTarget(RefactorTarget target) {
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
                // TODO impl
            }
        } else if (target instanceof RefactorTarget.Statement) {
            MethodStatement m = ((MethodStatement)((RefactorTarget.Statement) target).getStatement());

            if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                return new MethodStatementIdentifierRefactorChange(m);
            } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                // TODO impl
            }
        }
        throw new RuntimeException("invalid refactor target type: " + target.getClass());
    }

    @Override
    public void setRefactorDescriptor(RefactorDescriptor descriptor) {
        this.refactorDescriptor = Objects.requireNonNull(descriptor);
    }

    @Override
    public void setSearchResults(List<RefactorTarget> searchResults) {
        this.searchResults = Objects.requireNonNull(searchResults);
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    private List<Result> refactorMethodRename(Path file, List<RefactorChange> changes) throws IOException {
        // Sort changes to prevent indexes from being out of sync
        changes.sort(new RenameComparator());

        // Prepare to make changes
        String newName = ((RefactorDescriptor.Rename) refactorDescriptor).getIdentifierName();
        List<String> lines = Files.readAllLines(file);
        List<Result> results = new ArrayList<>();

        // Modify file
        changes.forEach(r -> {
            String result = rename(lines, newName, r);
            results.add(new Result(file, r.lineNumber(), result));
        });

        // Write file
        Files.write(file, lines);
        return results;
    }

    private String rename(List<String> lines, String newName, RefactorChange r) {
        int lineNo = r.lineNumber() - 1;
        int startIdx = r.startIndex();
        int endIdx = r.endIndex();

        String code = lines.get(lineNo);
        String p1 = code.substring(0, startIdx);
        String p2 = code.substring(endIdx);
        String newCode = p1 + newName + p2;
        lines.set(lineNo, newCode);
        LOGGER.info("Rename: {},{},{}: {} => {}", lineNo, startIdx, endIdx, code, newCode);
        return newCode;
    }

    private List<Result> refactorMethodChangeParameters(Path file, List<RefactorChange> changes) {
        List<ParameterVariableDescriptor> descriptors = ((RefactorDescriptor.ChangeParameters)refactorDescriptor)
                .getDescriptors();

        // TODO impl
        return null;
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
                        if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                            results.addAll(refactorMethodRename(file, searchResults));
                        } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                            results.addAll(refactorMethodChangeParameters(file, searchResults));
                        } else {
                            throw new UnsupportedOperationException("unsupported refactor target: "
                                    + refactorDescriptor.getClass().getName());
                        }
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

    /**
     * A comparator for renaming refactoring. This sorts in descending order using the identifier name's start index.
     */
    private static final class RenameComparator implements Comparator<RefactorChange> {

        @Override
        public int compare(RefactorChange o1, RefactorChange o2) {
            return o2.startIndex() - o1.startIndex();
        }
    }
}
