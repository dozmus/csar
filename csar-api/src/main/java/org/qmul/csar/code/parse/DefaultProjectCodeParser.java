package org.qmul.csar.code.parse;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.ConcurrentIterator;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A multi-threaded project-wide code parser.
 */
public class DefaultProjectCodeParser extends MultiThreadedTaskProcessor implements ProjectCodeParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProjectCodeParser.class);
    private final ConcurrentIterator<Path> it;
    private final ConcurrentHashMap<Path, Statement> results = new ConcurrentHashMap<>();
    private final TotalFileSizes totalFileSizes = new TotalFileSizes();
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
        super(threadCount, "csar-parse");
        this.it = new ConcurrentIterator<>(Objects.requireNonNull(it));
        this.factory = Objects.requireNonNull(factory);
        setRunnable(new Task());
    }

    /**
     * Returns a map with parsed files as keys, and the output statements as values.
     * If {@link #it} contains no files then an empty map is returned.
     *
     * @return a map with parsed files as keys, and the output statements as values.
     */
    public Map<Path, Statement> results() {
        // Check if iterate has items
        if (!it.hasNext()) {
            return results;
        }

        // Execute and return results
        long startTime = System.currentTimeMillis();
        run();

        // Log completion message
        LOGGER.debug("Parsed {}kb of code in {}ms", totalFileSizes.sizeKb,
                (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
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

    private static final class TotalFileSizes {

        private long sizeKb = 0;

        public void add(long sizeBytes) {
            sizeKb += Math.round((double)sizeBytes / 1000D);
        }
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            Path file = null;
            Statement root;

            try {
                while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                    // Get the next file
                    file = it.next();
                    String fileName = file.getFileName().toString();
                    LOGGER.trace("Parsing: {}", fileName);

                    try {
                        // Parse file and put in the map
                        root = factory.create(file).parse(file);
                        results.put(file, root);

                        // Update statistics
                        synchronized (totalFileSizes) {
                            totalFileSizes.add(Files.size(file));
                        }
                    } catch (IOException | RuntimeException ex) {
                        Path finalFile = file;
                        errorListeners.forEach(l -> l.errorParsing(finalFile, ex));
                    }
                }
            } catch (Exception ex) {
                Path finalFile = file;
                errorListeners.forEach(l -> l.fatalErrorParsing(finalFile, ex));
                terminate();
            }
        }
    }
}
