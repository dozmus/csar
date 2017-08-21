package org.qmul.csar;

import com.beust.jcommander.Parameter;

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

    @Parameter(names = {"--help", "-h"}, description = "Help information", help = true, order = 4)
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

    public boolean isPrintHelp() {
        return printHelp;
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
        if (path.toFile().isDirectory())
            return false;
        String fileName = path.getFileName().toString();
        return Arrays.stream(HANDLED_CODE_FILE_EXTENSIONS).anyMatch(fileName::endsWith);
    }
}
