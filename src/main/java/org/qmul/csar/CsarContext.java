package org.qmul.csar;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import org.qmul.csar.result.PlainTextResultFormatter;
import org.qmul.csar.result.ResultFormatter;
import org.qmul.csar.util.ResultFormatterConverter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line arguments for {@link Csar#main(String[])}.
 */
public class CsarContext {

    @Parameter(description = "Search query", required = true, order = 1)
    private List<String> query = new ArrayList<>();

    @Parameter(names = {"--threads", "-t"}, description = "Thread count", order = 2)
    private int threads = 1;

    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output", order = 3)
    private boolean verbose;

    @Parameter(names = {"--format", "-f"}, description = "Output format", order = 4,
            converter = ResultFormatterConverter.class)
    private ResultFormatter resultFormatter = new PlainTextResultFormatter();

    @Parameter(names = {"--output", "-o"}, description = "Output file name", order = 5, converter = PathConverter.class)
    private Path path;

    @Parameter(names = {"--project-url", "--url"}, description = "Print project URL", order = 6)
    private boolean printProjectUrl;

    @Parameter(names = {"--help", "-h"}, description = "Print help information", help = true, order = 7)
    private boolean printHelp;

    public String getQuery() {
        return String.join(" ", query);
    }

    public boolean isVerbose() {
        return verbose;
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

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isPrintProjectUrl() {
        return printProjectUrl;
    }

    public Path getDirectory() {
        return Paths.get(".");
    }
}
