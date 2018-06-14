package org.qmul.csar;

import org.qmul.csar.plugin.CsarPluginLoader;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.code.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A code search and refactor tool instance.
 */
public class Csar {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final String query;
    private final int threadCount;
    private final Path projectDirectory;
    private final boolean narrowSearch;
    private final Path ignoreFile;
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private CsarQuery csarQuery;
    private CsarPluginLoader pluginLoader;
    private List<Result> searchResults;
    private ArrayList<Result> refactorResults;
    private boolean errorOccurred = false;

    /**
     * Constructs a new {@link Csar} with the given arguments, this will also add an error listener of type
     * {@link DefaultCsarErrorListener}. If this is not present Csar will malfunction.
     *
     * @param query the csar query to perform
     * @param threadCount the amount of threads to use
     * @param projectDirectory the project directory
     * @param narrowSearch if the search domain should be narrowed
     * @param ignoreFile the csar ignore file to use
     */
    public Csar(String query, int threadCount, Path projectDirectory, boolean narrowSearch, Path ignoreFile) {
        this.query = Objects.requireNonNull(query);
        this.threadCount = threadCount;
        this.projectDirectory = Objects.requireNonNull(projectDirectory);
        this.narrowSearch = narrowSearch;
        this.ignoreFile = Objects.requireNonNull(ignoreFile);

        // Add default error listener, which sets errorOccurred
        addErrorListener(new DefaultCsarErrorListener(this));
    }

    /**
     * Initializes and then runs Csar.
     */
    public void run() {
        init();
        parseQuery();
        parse();
        postprocess();
        search();
        refactor();
    }

    /**
     * Initializes Csar by loading plugins.
     */
    public void init() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        LOGGER.info("Loading plugins...");

        // Create the plugin loader
        pluginLoader = new CsarPluginLoader();

        if (!pluginLoader.plugins().hasNext()) {
            errorListeners.forEach(CsarErrorListener::fatalErrorInitializing);
        }

        // Add error handlers to each plugin
        errorListeners.forEach(l -> pluginLoader.forEachPlugin(p -> p.addErrorListener(l)));
    }

    /**
     * Parses the csar query.
     */
    public void parseQuery() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");

        try {
            csarQuery = CsarQueryFactory.parse(query.trim());
        } catch (Exception ex) {
            errorListeners.forEach(l -> l.fatalErrorParsingCsarQuery(ex));
        }
    }

    /**
     * Parses the project code on each underlying language-specific plugin.
     */
    public void parse() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        pluginLoader.forEachPlugin(p -> {
            try {
                p.parse(projectDirectory, narrowSearch, ignoreFile, threadCount);
            } catch (Exception e) {
                // do nothing, this is handled in the error listeners
            }
        });
    }

    /**
     * Post-processing the project code on each underlying language-specific plugin.
     */
    public void postprocess() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        pluginLoader.forEachPlugin(p -> p.postprocess(threadCount));
    }

    /**
     * Searches the project code on each underlying language-specific plugin.
     */
    public void search() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        searchResults = new ArrayList<>();

        pluginLoader.forEachPlugin(p -> {
            List<Result> results = p.search(csarQuery, threadCount);
            this.searchResults.addAll(results);
        });
    }

    /**
     * Refactors the project code on each underlying language-specific plugin.
     */
    public void refactor() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");

        csarQuery.getRefactorDescriptor().ifPresent(refactorDescriptor -> {
            // Print warnings
            for (String warning : refactorDescriptor.warnings()) {
                LOGGER.info(warning);
            }

            // Apply refactor
            refactorResults = new ArrayList<>();

            pluginLoader.forEachPlugin(p -> {
                List<Result> results = p.refactor(csarQuery, searchResults, threadCount);
                this.refactorResults.addAll(results);
            });
        });

    }

    /**
     * Returns the search results, aggregated from each underlying language-specific plugin.
     * This will be a partial list if an unrecoverable error occurred while searching, and <tt>null</tt> if searching
     * was never initiated.
     *
     * @return search results.
     */
    public List<Result> getSearchResults() {
        return searchResults;
    }

    /**
     * Returns the refactor results, aggregated from each underlying language-specific plugin.
     * This will be a partial list if an unrecoverable error occurred while searching, and <tt>null</tt> if searching
     * was never initiated.
     *
     * @return refactor results.
     */
    public List<Result> getRefactorResults() {
        return refactorResults;
    }

    /**
     * Adds an error listener.
     *
     * @param errorListener the error listener
     */
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    /**
     * Removes an error listener.
     *
     * @param errorListener the error listener
     */
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }


    /**
     * Returns if an error occurred.
     */
    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    /**
     * This tells Csar that an error occurred, do not use this sporadically!
     */
    void setErrorOccurred() {
        this.errorOccurred = true;
    }
}
