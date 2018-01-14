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

/**
 * A code search and refactor tool instance.
 */
public class Csar {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final String query;
    private final int threadCount;
    private final boolean benchmarking;
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
     * @param projectDirectory
     * @param ignoreFile
     */
    public Csar(String query, int threadCount, boolean benchmarking, Path projectDirectory,
            boolean narrowSearch, Path ignoreFile) {
        this.query = query;
        this.threadCount = threadCount;
        this.benchmarking = benchmarking;
        this.projectDirectory = projectDirectory;
        this.narrowSearch = narrowSearch;
        this.ignoreFile = ignoreFile;
    }

    /**
     * Loads plugins.
     */
    public void init() {
        LOGGER.info("Loading plugins...");

        // Create the plugin manager
        PluginManager pluginManager = new DefaultPluginManager() {
            @Override
            protected CompoundPluginDescriptorFinder createPluginDescriptorFinder() {
                return new CompoundPluginDescriptorFinder()
                        .add(new ManifestPluginDescriptorFinder());
            }
        };
        LOGGER.info("Plugins Directory: {}", pluginManager.getPluginsRoot().toAbsolutePath());
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        // Retrieves the extensions
        plugins = pluginManager.getExtensions(CsarPlugin.class);

        if (plugins.size() == 0) {
            LOGGER.error("Found no suitable plugins.");
        }
    }

    /**
     * Returns <tt>true</tt> if {@link #query} was parsed and assigned to {@link #csarQuery} successfully.
     * @return <tt>true</tt> if the query was parsed successfully.
     */
    public boolean parseQuery() {
        LOGGER.trace("Parsing query...");

        try {
            csarQuery = CsarQueryFactory.parse(query);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
            return false;
        }
        return true;
    }

    public void parseCode() {
        LOGGER.trace("Parsing code...");

        for (CsarPlugin plugin : plugins) {
            plugin.parse(projectDirectory, narrowSearch, ignoreFile, threadCount, benchmarking);
        }
    }

    public void postprocess() {
        LOGGER.trace("Post processing...");

        for (CsarPlugin plugin : plugins) {
            plugin.postprocess(benchmarking);
        }
    }

    public void searchCode() {
        LOGGER.trace("Searching code...");
        results = new ArrayList<>();

        for (CsarPlugin plugin : plugins) {
            List<Result> results = plugin.search(csarQuery, threadCount);
            this.results.addAll(results);
        }
    }

    public List<Result> getResults() {
        return results;
    }
}
