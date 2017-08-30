package org.qmul.csar;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import org.qmul.csar.result.PlainTextResultFormatter;
import org.qmul.csar.util.ResultFormatterConverter;
import org.qmul.csar.result.ResultFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Command-line arguments for {@link Csar#main(String[])}.
 */
public class CsarContext {

    /**
     * An array of handled code file extensions.
     */
    private static final String[] HANDLED_CODE_FILE_EXTENSIONS = {".java"};

    @Parameter(names = {"--query", "-q"}, description = "Search query", required = true, order = 1)
    private String query;

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
        return query;
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

    public boolean isGitRepository() {
        return Files.isDirectory(Paths.get(".git"));
    }

    /**
     * @return If the given file is not a directory and is handled by csar.
     * @see {@link #HANDLED_CODE_FILE_EXTENSIONS}
     */
    public boolean accepts(Path path) {
        if (Files.isDirectory(path))
            return false;
        String fileName = path.getFileName().toString();
        return Arrays.stream(HANDLED_CODE_FILE_EXTENSIONS).anyMatch(fileName::endsWith);
    }
}
