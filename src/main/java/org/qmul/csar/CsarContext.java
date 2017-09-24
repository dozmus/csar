package org.qmul.csar;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import org.qmul.csar.result.PlainTextResultFormatter;
import org.qmul.csar.result.ResultFormatter;
import org.qmul.csar.util.ResultFormatterConverter;
import org.qmul.csar.util.Slf4jLevelConverter;
import org.slf4j.event.Level;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line arguments.
 *
 * @see Main#main(String[])
 * @see Main#printUsage()
 * @see Csar
 */
public class CsarContext {

    @Parameter(description = "Search query", required = true, order = 1)
    private List<String> query = new ArrayList<>();

    @Parameter(names = {"--threads", "-t"}, description = "Thread count", order = 2)
    private int threads = 1;

    @Parameter(names = {"--log-level"}, description = "Log level", order = 3, converter = Slf4jLevelConverter.class)
    private Level logLevel = Level.INFO;

    @Parameter(names = {"--format", "-f"}, description = "Output format", order = 4,
            converter = ResultFormatterConverter.class)
    private ResultFormatter resultFormatter = new PlainTextResultFormatter();

    @Parameter(names = {"--output", "-o"}, description = "Output file name", order = 5, converter = PathConverter.class)
    private Path path;

    @Parameter(names = {"--narrow-search"}, description = "Narrow search domain", order = 6)
    private boolean narrowSearch = true;

    @Parameter(names = {"--project-url", "--url"}, description = "Print project URL", order = 7)
    private boolean printProjectUrl;

    @Parameter(names = {"--help", "-h"}, description = "Print help information", order = 8, help = true)
    private boolean printHelp;

    public String getQuery() {
        return String.join(" ", query);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public int getThreads() {
        return threads;
    }

    public ResultFormatter getResultFormatter() {
        return resultFormatter;
    }

    public Path getPath() {
        return path;
    }

    public boolean isNarrowSearch() {
        return narrowSearch;
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isPrintProjectUrl() {
        return printProjectUrl;
    }

    /**
     * Returns the project base directory.
     * @return the project base directory
     */
    public Path getDirectory() {
        return Paths.get(".");
    }
}
