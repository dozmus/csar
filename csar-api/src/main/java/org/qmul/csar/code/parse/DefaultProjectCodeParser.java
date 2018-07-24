package org.qmul.csar.code.parse;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.parse.cache.DummyProjectCodeCache;
import org.qmul.csar.code.parse.cache.OutdatedCacheException;
import org.qmul.csar.code.parse.cache.ProjectCodeCache;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

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
    private final Supplier<ProjectCodeCache> cacheSupplier;

    /**
     * Creates a new {@link DefaultProjectCodeParser} with a <tt>threadCount</tt> of <tt>1</tt>, and no caching.
     */
    public DefaultProjectCodeParser(CodeParserFactory factory, Iterator<Path> it) {
        this(factory, it, DummyProjectCodeCache::new, 1);
    }

    /**
     * Creates a new {@link DefaultProjectCodeParser} with no caching.
     */
    public DefaultProjectCodeParser(CodeParserFactory factory, Iterator<Path> it, int threadCount) {
        this(factory, it, DummyProjectCodeCache::new, threadCount);
    }

    /**
     * Creates a new {@link DefaultProjectCodeParser}.
     *
     * @param factory the {@link CodeParserFactory} to use to create parsers
     * @param it the {@link Path} iterator whose contents to parse
     * @param cacheSupplier the cache supplier each thread will use to obtain a cache
     * @param threadCount the amount of threads to use
     * @throws IllegalArgumentException if <tt>threadCount</tt> is less than or equal to <tt>0</tt>
     * @throws NullPointerException if it or factory is <tt>null</tt>
     */
    public DefaultProjectCodeParser(CodeParserFactory factory, Iterator<Path> it,
            Supplier<ProjectCodeCache> cacheSupplier, int threadCount) {
        super(threadCount, "csar-parse");
        this.it = new ConcurrentIterator<>(Objects.requireNonNull(it));
        this.factory = Objects.requireNonNull(factory);
        this.cacheSupplier = Objects.requireNonNull(cacheSupplier);
        setRunnable(new Task());
    }

    /**
     * Returns a map with parsed files as keys, and the output statements as values.
     * If {@link #it} contains no files then an empty map is returned.
     *
     * @return a map with parsed files as keys, and the output statements as values.
     */
    public CodeBase results() {
        LOGGER.info("Starting...");

        // Check if iterate has items
        if (!it.hasNext()) {
            return new CodeBase(results);
        }

        // Execute and return results
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        statistics.debug(stopwatch.elapsedMillis());
        LOGGER.info("Finished");
        return new CodeBase(results);
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

        private AtomicLong sizeKb = new AtomicLong();
        /**
         * The number of lines in the parsed source code. This closely resembles the LOC figure, but will not be equal
         * to it in most cases.
         */
        private AtomicLong linesOfCode = new AtomicLong();
        private AtomicInteger filesProcessed = new AtomicInteger();
        private AtomicInteger cacheHits = new AtomicInteger();

        /**
         * Updates figures to account for a newly parsed file.
         */
        public void update(long sizeB, long lineCount) {
            sizeKb.addAndGet(Math.round(sizeB / 1000D));
            linesOfCode.addAndGet(lineCount);
            filesProcessed.incrementAndGet();
        }

        /**
         * Logs statistics to the debug channel.
         */
        public void debug(long ellapsedMillis) {
            LOGGER.debug("Parsed {}kb of code in {}ms over {} files containing {} LOC with {} cache hits",
                    sizeKb, ellapsedMillis, filesProcessed, linesOfCode, cacheHits);
        }
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            Path file = null;
            ProjectCodeCache cache = cacheSupplier.get();

            try {
                while (!Thread.currentThread().isInterrupted() && it.hasNext()) {
                    // Get the next file
                    try {
                        file = it.next();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    LOGGER.trace("Parsing: {}", file.getFileName().toString());

                    try {
                        // Parse file and put in the map
                        Statement root;

                        try {
                            // Check cache
                            root = cache.get(file);
                            statistics.cacheHits.incrementAndGet();
                        } catch (FileNotFoundException | OutdatedCacheException e) {
                            // Parse from file
                            root = factory.create(file).parse(file);

                            // Cache
                            try {
                                cache.put(file, root);
                            } catch (IOException ex) {
                                LOGGER.warn("Failed to put file {} into the cache because {}",
                                        file.toAbsolutePath().toString(), ex.getMessage());
                                LOGGER.debug("Failed to put file into the cache.", ex);
                            }
                        }
                        results.put(file, root);

                        // Update statistics
                        statistics.update(Files.size(file), Files.lines(file).count());
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
