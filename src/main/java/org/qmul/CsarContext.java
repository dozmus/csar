package org.qmul;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.List;

/**
 * Command-line arguments for {@link Csar#main(String[])}.
 */
public final class CsarContext {

    @Parameter(description = "Input files", required = true)
    private List<File> inputFiles; // XXX wildcards, etc. are handled by the shell invoking the program

    @Parameter(names = {"--query", "-q"}, description = "Search query", required = true, order = 1)
    private String query;

    @Parameter(names = {"--threads", "-t"}, description = "Thread count", order = 2)
    private int threads = 1;

    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output", order = 3)
    private boolean verbose; // TODO use alongside a logging framework?

    @Parameter(names = {"--help", "-h"}, description = "Help information", help = true, order = 4)
    private boolean printHelp;

    public List<File> getInputFiles() {
        return inputFiles;
    }

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
}
