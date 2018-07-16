package org.qmul.csar.code.java.refactor;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.refactor.writer.DefaultRefactorChangeWriter;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.code.refactor.writer.RefactorChangeWriter;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.Stopwatch;
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
    private final RefactorChangeWriter writer;
    private RefactorDescriptor refactorDescriptor;
    private List<SerializableCode> searchResultObjects;
    private ConcurrentIterator<Map.Entry<Path, List<RefactorChange>>> it;
    private TypeHierarchyResolver thr;

    /**
     * Constructs a new {@link JavaCodeRefactorer} with {@link DefaultRefactorChangeWriter}.
     *
     * @param threadCount the amount of threads to use
     */
    public JavaCodeRefactorer(int threadCount, TypeHierarchyResolver thr) {
        this(threadCount, thr, new DefaultRefactorChangeWriter());
    }

    /**
     * Constructs a new {@link JavaCodeRefactorer} with the given arguments.
     *
     * @param threadCount the amount of threads to use
     */
    public JavaCodeRefactorer(int threadCount, TypeHierarchyResolver thr, RefactorChangeWriter writer) {
        super(threadCount, "csar-refactor");
        this.writer = writer;
        this.thr = thr;
        setRunnable(new Task());
    }

    @Override
    public List<Result> results() {
        LOGGER.info("Starting...");

        // Group search results by file, to allow multi-threading without resource races
        List<RefactorChange> changes = RefactorChangeHelper.create(refactorDescriptor, searchResultObjects, thr);
        Map<Path, List<RefactorChange>> groupedChanges = RefactorChangeHelper.groupByFile(changes);
        it = new ConcurrentIterator<>(groupedChanges.entrySet().iterator());

        // Execute and return results
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        LOGGER.debug("Time taken: {}ms", stopwatch.elapsedMillis());
        LOGGER.info("Finished");
        return results;
    }

    @Override
    public void setRefactorDescriptor(RefactorDescriptor descriptor) {
        this.refactorDescriptor = Objects.requireNonNull(descriptor);
    }

    @Override
    public void setSearchResultObjects(List<SerializableCode> searchResultObjects) {
        this.searchResultObjects = Objects.requireNonNull(searchResultObjects);
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
            List<RefactorChange> changes;

            try {
                while (!Thread.currentThread().isInterrupted() && it.hasNext()) {
                    // Get the next entry
                    try {
                        entry = it.next();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    file = entry.getKey();
                    changes = entry.getValue();
                    String fileName = file.getFileName().toString();
                    LOGGER.trace("Refactoring {}", fileName);

                    try {
                        // Refactor file and store the results
                        List<Result> tmpResults = writer.writeAll(changes);
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
