package org.qmul;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.List;

/**
 * Command-line arguments for {@link Csar#main(String[])}.
 */
public class CsarContext {

    @Parameter(description = "[Input files]", required = true)
    private List<File> inputFiles; // XXX expansion of regex, etc. should be done by the shell invoking the program

    @Parameter(names = {"--query", "-q"}, description = "Search query", required = true)
    private String query;

    @Parameter(names = {"--verbose", "-v"}, description = "Verbose output")
    private boolean verbose; // TODO use alongside a logging framework?

    @Parameter(names = {"--threads", "-t"}, description = "Thread count")
    private int threads = 1;

    @Parameter(names = {"--help", "-h"}, description = "Help information", help = true)
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
