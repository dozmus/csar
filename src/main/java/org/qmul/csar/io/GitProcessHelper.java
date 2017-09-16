package org.qmul.csar.io;

import org.qmul.csar.util.ProcessHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *Miscellaneous utility methods for interacting with the git process.
 */
public class GitProcessHelper {

    /**
     * Runs <tt>git ls-files</tt> and returns its output as a <tt>List</tt>.
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    public static List<Path> lsFiles() throws Exception {
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

        if (output1.startsWith("fatal: Not a git repository")
                || output1.startsWith("'git' is not recognized as an internal or external command")) {
            throw new Exception("Error running git ls-files: no output");
        }

        // Map into a Path list
        return output.stream().map(fileName -> Paths.get(fileName)).collect(Collectors.toList());
    }
}
