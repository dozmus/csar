package org.qmul.csar.code.parse;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.Stopwatch;
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
    private final Statistics statistics = new Statistics();
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final CodeParserFactory factory;

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
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        LOGGER.debug("Parsed {}kb of code in {}ms over {} files containing {} LOC", statistics.sizeKb,
                stopwatch.elapsedMillis(), statistics.fileCount, statistics.linesOfCode);
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

    private static final class Statistics {

        private long sizeKb;
        private long linesOfCode;
        private int fileCount;

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
                while (!Thread.currentThread().isInterrupted() && it.hasNext()) {
                    // Get the next file
                    try {
                        file = it.next();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    String fileName = file.getFileName().toString();
                    LOGGER.trace("Parsing: {}", fileName);

                    try {
                        // Parse file and put in the map
                        root = factory.create(file).parse(file);
                        results.put(file, root);
                        long sizeB = Files.size(file);
                        long lineCount = Files.lines(file).count();

                        // Update statistics
                        synchronized (statistics) {
                            statistics.add(sizeB);
                            statistics.fileCount++;
                            statistics.linesOfCode += lineCount;
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
