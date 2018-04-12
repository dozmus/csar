package org.qmul.csar.io.it.vcs;

import org.qmul.csar.io.ProcessHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Iterates over files in the specified directory which is home to a git repository, by aggregating them.
 *
 * @see VCSRepositoryIterator
 */
public class GitRepositoryIterator extends VCSRepositoryIterator {

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public GitRepositoryIterator(Path directory) {
        super(directory, "git", ".git");
    }

    /**
     * Runs <tt>git ls-files</tt> and returns its output as a <tt>List</tt>.
     *
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    protected List<Path> lsFiles() throws Exception {
        List<String> output;

        try {
            Process p = ProcessHelper.run("git", "ls-files");
            output = ProcessHelper.readOutput(p);
            p.waitFor(5, TimeUnit.SECONDS);
            p.destroy();
        } catch (InterruptedException | IOException e) {
            throw new Exception("Error running git ls-files: " + e.getMessage());
        }

        // Check if git repository found
        if (output.size() == 0) {
            throw new Exception("Error running git ls-files: no output");
        }

        String output1 = output.get(0);

        if (output1.startsWith("fatal:")) {
            throw new Exception("Error running git ls-files: " + output1);
        }

        if (output1.startsWith("'git' is not recognized as an internal or external command")) {
            throw new Exception("Error running git ls-files: git is not recognized");
        }

        // Map into a Path list
        return output.stream()
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toList());
    }
}
