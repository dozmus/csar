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
 * Iterates over files in the specified directory which is home to a git repository, aggregating them.
 */
public class GitProjectIterator extends ProjectIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitProjectIterator.class);

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public GitProjectIterator(Path directory) {
        super(directory);
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", getDirectory().toString());
        boolean gitRepository = Files.isDirectory(Paths.get(getDirectory().toString(), ".git"));

        if (!gitRepository)
            throw new IllegalStateException("repository is missing a .git folder");
        scanGitDir();
        initialized = true;
    }

    /**
     * Finds code files in a git repository, which are in the staging area or have been committed. This is done by
     * creating an instance of the git program. Failure will result in throwing {@link RuntimeException}.
     *
     * @throws RuntimeException if an error occurs
     * @see <a href="https://git-scm.com/docs/git-ls-files">git ls-files</a>
     * @see #lsFiles()
     */
    private void scanGitDir() {
        LOGGER.trace("Scanning git repository");

        try {
            List<Path> output = lsFiles();
            output.forEach(this::addFile);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException("unable to scan git directory: " + ex.getMessage());
        }
    }

    /**
     * Runs <tt>git ls-files</tt> and returns its output as a <tt>List</tt>.
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    private static List<Path> lsFiles() throws Exception {
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
        return output.stream()
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toList());
    }
}
