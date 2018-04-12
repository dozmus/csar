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
 * Iterates over files in the specified directory which is home to a mercurial repository, aggregating them.
 */
public class MercurialProjectIterator extends ProjectIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitProjectIterator.class);

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public MercurialProjectIterator(Path directory) {
        super(directory);
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", getDirectory().toString());
        boolean mercurialRepository = Files.isDirectory(Paths.get(getDirectory().toString(), ".hg"));

        if (!mercurialRepository)
            throw new IllegalStateException("repository is missing a .hg folder");
        scanGitDir();
        initialized = true;
    }

    /**
     * Finds code files in a hg repository, which are in the staging area (added or modified) or have been committed.
     * This is done by creating an instance of the hg program. Failure will result in throwing {@link RuntimeException}.
     *
     * @throws RuntimeException if an error occurs
     * @see <a href="https://kapeli.com/cheat_sheets/Mercurial.docset/Contents/Resources/Documents/index#//dash_ref/Category/Work%20Status/1">hg status</a>
     * @see #lsFiles()
     */
    private void scanGitDir() {
        LOGGER.trace("Scanning hg repository");

        try {
            List<Path> output = lsFiles();
            output.forEach(this::addFile);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException("unable to scan hg directory: " + ex.getMessage());
        }
    }

    /**
     * Runs <tt>hg status --all</tt> and returns its output as a <tt>List</tt>.
     *
     * @return a list of the output paths
     * @throws Exception an error occurred while reading output
     */
    private static List<Path> lsFiles() throws Exception {
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
