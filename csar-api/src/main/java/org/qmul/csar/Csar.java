package org.qmul.csar;

import org.pf4j.*;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.result.Result;
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
    private CsarQuery csarQuery;
    private List<Result> results;
    private List<CsarPlugin> plugins;

    /**
     * Constructs a new {@link Csar} with the given arguments.
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
    }

    /**
     * Loads plugins.
     * @return <tt>true</tt> if suitable plugins were found.
     */
    public boolean init() {
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
            LOGGER.error("Found no suitable plugins.");
            return false;
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> if {@link #query} was parsed and assigned to {@link #csarQuery} successfully.
     * @return <tt>true</tt> if the query was parsed successfully.
     */
    public boolean parseQuery() {
        LOGGER.info("Parsing query...");

        try {
            csarQuery = CsarQueryFactory.parse(query);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Returns if parsing the project code was successful on each underlying language-specific plugin.
     * @return if parsing was successful
     */
    public boolean parseCode() {
        LOGGER.info("Parsing code...");

        for (CsarPlugin plugin : plugins) {
            if (!plugin.parse(projectDirectory, narrowSearch, ignoreFile, threadCount)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if post-processing the project code was successful on each underlying language-specific plugin.
     * @return if post-processing was successful
     */
    public boolean postprocess() {
        LOGGER.info("Post processing...");

        for (CsarPlugin plugin : plugins) {
            if (!plugin.postprocess()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if searching the project code was successful on each underlying language-specific plugin.
     * @return if searching was successful
     */
    public boolean searchCode() {
        LOGGER.info("Searching code...");
        results = new ArrayList<>();

        for (CsarPlugin plugin : plugins) {
            try {
                List<Result> results = plugin.search(csarQuery, threadCount);
                this.results.addAll(results);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the search results, aggregated from each underlying language-specific plugin.
     * This may be a partial list if an unrecoverable error occurred while searching.
     *
     * @return search results.
     */
    public List<Result> getResults() {
        return results;
    }
}
