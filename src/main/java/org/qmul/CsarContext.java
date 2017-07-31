package org.qmul;

import com.beust.jcommander.Parameter;
import org.qmul.io.ProjectFileScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line arguments for {@link Csar#main(String[])}.
 */
public final class CsarContext {

    @Parameter(names = {"--query", "-q"}, description = "Search query", required = true, order = 1)
    private String query;

    @Parameter(names = {"--threads", "-t"}, description = "Thread count", order = 2)
    private int threads = 1;

    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output", order = 3)
    private boolean verbose; // TODO use alongside a logging framework?

    @Parameter(names = {"--help", "-h"}, description = "Help information", help = true, order = 4)
    private boolean printHelp;

    private final List<ProjectFileScanner.CodeFile> codeFiles = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public int getThreads() {
        return threads;
    }

    public List<ProjectFileScanner.CodeFile> getCodeFiles() {
        return codeFiles;
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
}
