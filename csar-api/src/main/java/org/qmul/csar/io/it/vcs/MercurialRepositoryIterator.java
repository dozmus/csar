package org.qmul.csar.io.it.vcs;

import org.qmul.csar.io.ProcessHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Iterates over files in the specified directory which is home to a mercurial repository, by aggregating them.
 *
 * @see VCSRepositoryIterator
 */
public class MercurialRepositoryIterator extends VCSRepositoryIterator {

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public MercurialRepositoryIterator(Path directory) {
        super(directory, "hg", ".hg");
    }

    /**
     * Runs <tt>hg status --all</tt> and returns its output as a <tt>List</tt>.
     *
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    protected List<Path> lsFiles() throws Exception {
        List<String> output;

        try {
            Process p = ProcessHelper.run("hg", "status", "--all");
            output = ProcessHelper.readOutput(p);
            p.waitFor(5, TimeUnit.SECONDS);
            p.destroy();
        } catch (InterruptedException | IOException e) {
            throw new Exception("Error running hg status --all: " + e.getMessage());
        }

        // Check if hg repository found
        if (output.size() == 0) {
            throw new Exception("Error running hg status --all: no output");
        }

        String output1 = output.get(0);

        if (output1.startsWith("abort:")) {
            throw new Exception("Error running hg status --all: " + output1);
        }

        if (output1.startsWith("'hg' is not recognized as an internal or external command")) {
            throw new Exception("Error running hg status --all: hg is not recognized");
        }

        // Map into a Path list
        return output.stream()
                .filter(s -> s.startsWith("A") || s.startsWith("C") || s.startsWith("M")) // added, committed, modified
                .map(s -> s.substring(s.indexOf(' ') + 1)) // remove A/C/M flag
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toList());
    }
}
