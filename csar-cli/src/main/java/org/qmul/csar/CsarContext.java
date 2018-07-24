package org.qmul.csar;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import org.qmul.csar.result.formatter.PlainTextResultFormatter;
import org.qmul.csar.result.formatter.ResultFormatter;
import org.qmul.csar.util.ResultFormatterConverter;
import org.qmul.csar.util.Slf4jLevelConverter;
import org.slf4j.event.Level;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The command-line arguments of Csar, these define what it should do.
 *
 * @see Main#main(String[])
 * @see Main#printUsage()
 * @see Csar
 */
class CsarContext {

    @Parameter(description = "Search query", required = true)
    private List<String> query = new ArrayList<>();
    /**
     * The root directory of the project.
     */
    @Parameter(names = {"--directory", "-d"}, description = "Target directory", converter = PathConverter.class)
    private Path projectDirectory = Paths.get(".");
    /**
     * The csar directory.
     */
    @Parameter(names = {"--csardirectory", "-c"}, description = "Target directory", converter = PathConverter.class)
    private Path csarDirectory = Paths.get(".csar");
    @Parameter(names = {"--threads", "-t"}, description = "Thread count")
    private int threads = 1;
    @Parameter(names = {"--log-level"}, description = "Log level", converter = Slf4jLevelConverter.class)
    private Level logLevel = Level.INFO;
    @Parameter(names = {"--format", "-f"}, description = "Output format", converter = ResultFormatterConverter.class)
    private ResultFormatter resultFormatter = new PlainTextResultFormatter();
    @Parameter(names = {"--narrow-search"}, description = "Narrow search domain")
    private boolean narrowSearch = true;
    @Parameter(names = {"--ignore-file"}, description = "Ignore file", converter = PathConverter.class)
    private Path ignoreFile = Paths.get(".csarignore");
    @Parameter(names = {"--no-cache"}, description = "Do not use caching")
    private boolean noCache;
    @Parameter(names = {"--clear-cache"}, description = "Clears the cache")
    private boolean clearCache;
    @Parameter(names = {"--project-url", "--url"}, description = "Print project URL")
    private boolean printProjectUrl;
    @Parameter(names = {"--help", "-h"}, description = "Print help information", help = true)
    private boolean printHelp;

    /**
     * Returns the result of joining together {@link #query} with the space delimiter.
     */
    public String getQuery() {
        return String.join(" ", query);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public ResultFormatter getResultFormatter() {
        return resultFormatter;
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isPrintProjectUrl() {
        return printProjectUrl;
    }

    /**
     * Returns a new instance of {@link Csar} with the details contained in this class, and with the standard
     * {@link CliCsarErrorListener}.
     */
    public Csar createCsar() {
        Csar csar = new Csar(getQuery(), threads, csarDirectory, projectDirectory, narrowSearch, ignoreFile, noCache,
                clearCache);
        csar.addErrorListener(new CliCsarErrorListener());
        return csar;
    }
}
