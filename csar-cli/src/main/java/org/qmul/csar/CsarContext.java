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
 * A representation of the tasks Csar should carry out. This is also used to represent command-line arguments.
 *
 * @see Main#main(String[])
 * @see Main#printUsage()
 * @see Csar
 */
class CsarContext {

    @Parameter(description = "Search query", required = true, order = 0)
    private List<String> query = new ArrayList<>();
    /**
     * The root directory of the project.
     */
    @Parameter(names = {"--directory", "-d"}, description = "Target directory", order = 1,
            converter = PathConverter.class)
    private Path projectDirectory = Paths.get(".");
    @Parameter(names = {"--threads", "-t"}, description = "Thread count", order = 2)
    private int threads = 1;
    @Parameter(names = {"--log-level"}, description = "Log level", order = 3, converter = Slf4jLevelConverter.class)
    private Level logLevel = Level.INFO;
    @Parameter(names = {"--format", "-f"}, description = "Output format", order = 4,
            converter = ResultFormatterConverter.class)
    private ResultFormatter resultFormatter = new PlainTextResultFormatter();
    @Parameter(names = {"--narrow-search"}, description = "Narrow search domain", order = 6)
    private boolean narrowSearch = true;
    @Parameter(names = {"--ignore-file"}, description = "Ignore file", order = 7, converter = PathConverter.class)
    private Path ignoreFile = Paths.get(".csarignore");
    @Parameter(names = {"--project-url", "--url"}, description = "Print project URL", order = 9)
    private boolean printProjectUrl;
    @Parameter(names = {"--help", "-h"}, description = "Print help information", order = 10, help = true)
    private boolean printHelp;
    private CliCsarErrorListener errorListener;

    /**
     * Returns the result of joining together {@link #query} with the space delimiter.
     *
     * @return {@link #query} joined together with the space delimiter
     */
    public String getQuery() {
        return String.join(" ", query);
    }

    public int getThreads() {
        return threads;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public ResultFormatter getResultFormatter() {
        return resultFormatter;
    }

    public boolean isNarrowSearch() {
        return narrowSearch;
    }

    public Path getIgnoreFile() {
        return ignoreFile;
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isPrintProjectUrl() {
        return printProjectUrl;
    }

    /**
     * Returns the base project directory.
     */
    public Path getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * Returns a new instance of {@link Csar} with the details contained in this class, and with the standard
     * {@link CliCsarErrorListener}.
     */
    public Csar createCsar() {
        Csar csar = new Csar(getQuery(), threads, projectDirectory, narrowSearch, ignoreFile);
        csar.addErrorListener(new CliCsarErrorListener());
        return csar;
    }
}
