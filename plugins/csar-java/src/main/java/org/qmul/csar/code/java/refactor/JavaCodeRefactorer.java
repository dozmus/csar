package org.qmul.csar.code.java.refactor;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.refactor.refactorer.RefactorerFactory;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;
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
    private List<SerializableCode> searchResultObjects;
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

        // Group search results by file, to allow multi-threading without resource races
        Map<Path, List<RefactorChange>> groupedChanges
                = new RefactorChangeFactory(refactorDescriptor, searchResultObjects).create().groupByFile();
        it = new ConcurrentIterator<>(groupedChanges.entrySet().iterator());

        // Execute and return results
        long startTime = System.currentTimeMillis();
        run();

        // Log completion message
        LOGGER.debug("Time taken: {}ms", (System.currentTimeMillis() - startTime));
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
