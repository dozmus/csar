package org.qmul.csar;

import org.pf4j.*;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

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
    private List<Result> results;
    private List<CsarPlugin> plugins;
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

        // Add default error listener
        addErrorListener(new DefaultCsarErrorListener(this));
    }

    /**
     * Initializes Csar by loading plugins.
     */
    public void init() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        LOGGER.info("Loading plugins...");

        // Create the plugin manager
        PluginManager pluginManager = new DefaultPluginManager() {
            @Override
            protected CompoundPluginDescriptorFinder createPluginDescriptorFinder() {
                return new CompoundPluginDescriptorFinder()
                        .add(new ManifestPluginDescriptorFinder());
            }
        };
        LOGGER.trace("Plugins Directory: {}", pluginManager.getPluginsRoot().toAbsolutePath());
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        // Retrieves the extensions
        plugins = pluginManager.getExtensions(CsarPlugin.class);

        if (plugins.size() == 0) {
            errorListeners.forEach(CsarErrorListener::errorInitializing);
        }

        // Sets the error listeners
        for (CsarPlugin plugin : plugins) {
            errorListeners.forEach(plugin::addErrorListener);
        }
    }

    /**
     * Parses the csar query.
     */
    public void parseQuery() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");

        try {
            csarQuery = CsarQueryFactory.parse(query);
        } catch (Exception ex) {
            errorListeners.forEach(l -> l.errorParsingCsarQuery(ex));
        }
    }

    /**
     * Parses the project code on each underlying language-specific plugin.
     */
    public void parseCode() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        plugins.forEach(plugin -> plugin.parse(projectDirectory, narrowSearch, ignoreFile, threadCount));
    }

    /**
     * Post-processing the project code on each underlying language-specific plugin.
     */
    public void postprocess() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        plugins.forEach(CsarPlugin::postprocess);
    }

    /**
     * Searching the project code on each underlying language-specific plugin.
     */
    public void searchCode() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        results = new ArrayList<>();

        for (CsarPlugin plugin : plugins) {
            List<Result> results = plugin.search(csarQuery, threadCount);
            this.results.addAll(results);
        }
    }

    /**
     * Returns the search results, aggregated from each underlying language-specific plugin.
     * This may be a partial list if an unrecoverable error occurred while searching.
     *
     * @return search results.
     */
    public List<Result> getResults() {
        if (errorOccurred)
            throw new IllegalStateException("an error has occurred, csar cannot continue");
        return results;
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
     *
     * @return if an error occurred.
     */
    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    /**
     * This tells Csar that an error occurred, do not use this sporadically!
     */
    public void setErrorOccurred() {
        this.errorOccurred = true;
    }
}
