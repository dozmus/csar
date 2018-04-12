package org.qmul.csar.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Iterates over files in the specified directory which is home to a subversion repository, aggregating them.
 */
public class SubversionProjectIterator extends ProjectIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitProjectIterator.class);

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public SubversionProjectIterator(Path directory) {
        super(directory);
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", getDirectory().toString());
        boolean subversionRepository = Files.isDirectory(Paths.get(getDirectory().toString(), ".svn"));

        if (!subversionRepository)
            throw new IllegalStateException("repository is missing a .svn folder");
        scanGitDir();
        initialized = true;
    }

    /**
     * Finds code files in a svn repository, which are in the staging area (added or modified) or have been committed.
     * This is done by creating an instance of the hg program. Failure will result in throwing {@link RuntimeException}.
     *
     * @throws RuntimeException if an error occurs
     * @see <a href="http://svnbook.red-bean.com/en/1.7/svn.ref.svn.c.status.html">svn status</a>
     * @see #lsFiles()
     */
    private void scanGitDir() {
        LOGGER.trace("Scanning svn repository");

        try {
            List<Path> output = lsFiles();
            output.forEach(this::addFile);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException("unable to scan svn directory: " + ex.getMessage());
        }
    }

    /**
     * Runs <tt>svn status</tt> and returns its output as a <tt>List</tt>.
     *
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    private static List<Path> lsFiles() throws Exception {
        List<String> output;

        try {
            Process p = ProcessHelper.run("svn", "status");
            output = ProcessHelper.readOutput(p);
            p.waitFor(5, TimeUnit.SECONDS);
            p.destroy();
        } catch (InterruptedException | IOException e) {
            throw new Exception("Error running svn status: " + e.getMessage());
        }

        // Check if hg repository found
        if (output.size() == 0) {
            throw new Exception("Error running svn status: no output");
        }

        String output1 = output.get(0);

        if (output1.startsWith("svn: E:") || output1.startsWith("E:")) {
            throw new Exception("Error running svn status: " + output1);
        }

        if (output1.startsWith("'svn' is not recognized as an internal or external command")) {
            throw new Exception("Error running svn status: svn is not recognized");
        }

        // Map into a Path list
        return output.stream()
                .filter(s -> s.startsWith(" ") || s.startsWith("A") || s.startsWith("M")
                        || s.startsWith("R") || s.startsWith("C"))
                .map(s -> s.substring(8)) // removes all flags
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toList());
    }
}
