package org.qmul.csar.io;

import org.qmul.csar.code.CodeTreeParserFactory;
import org.qmul.csar.util.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates over files in the specified directory recursively to find accepted ones.
 * The code files may be narrowed down further if the folder is a git repository (if no error occurs).
 *
 * @see CodeTreeParserFactory#accepts(Path)
 * @see #scanGitDir()
 */
public class ProjectIterator implements PathIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIterator.class);
    private final boolean narrowSearch;
    private final Path directory;
    private final List<Path> files = new ArrayList<>(); // the element collection
    private int cursor = 0; // the index of the next element to return

    /**
     * Creates a new instance with the argument directory and <tt>narrowSearch</tt> set to <tt>true</tt>.
     *
     * @param directory the directory to search for files in
     */
    public ProjectIterator(Path directory) {
        this(directory, true);
    }

    /**
     * Creates a new instance.
     *
     * @param directory    the directory to search for files in
     * @param narrowSearch if the directory is home to a git repository, then if only files in the git repository
     *                     should be searched
     */
    public ProjectIterator(Path directory, boolean narrowSearch) {
        this.directory = directory;
        this.narrowSearch = narrowSearch;
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", directory.toString());
        files.clear();
        cursor = 0;
        boolean gitRepository = Files.isDirectory(Paths.get(directory.toString(), ".git"));

        // Find files
        if (gitRepository && narrowSearch) {
            scanGitDir();
        } else {
            scanDir();
        }
    }

    /**
     * Finds code files in a git repository, which are in the staging area or have been committed. This is done by
     * creating an instance of the git program. Failure will result in {@link #scanDir()} being called instead.
     *
     * @see <a href="https://git-scm.com/docs/git-ls-files">git ls-files</a>
     * @see CodeTreeParserFactory#accepts(Path)
     */
    private void scanGitDir() {
        LOGGER.trace("Scanning git repository");
        List<String> output;

        try {
            Process p = ProcessHelper.runAndWait("git", "ls-files");
            output = ProcessHelper.readOutput(p);
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Error running git ls-files: {}", e.getMessage());
            scanDir();
            return;
        }

        // Check if git repository found
        if (output.size() == 0) {
            LOGGER.error("Error running git ls-files: no output");
            scanDir();
            return;
        }

        String output1 = output.get(0);

        if (output1.startsWith("fatal: Not a git repository")
                || output1.startsWith("'git' is not recognized as an internal or external command")) {
            LOGGER.error("Error running git ls-files: {}", output1);
            scanDir();
            return;
        }

        for (String fileName : output) {
            Path path = Paths.get(fileName);

            if (CodeTreeParserFactory.accepts(path)) {
                addFile(path);
            }
        }
    }

    private void scanDir() {
        scanDir(directory, false);
    }

    /**
     * Scans the specified directory recursively, calling {@link #addFile(Path)} with files which are accepted.
     *
     * @param path The directory to be searched.
     * @see CodeTreeParserFactory#accepts(Path)
     */
    private void scanDir(Path path, boolean recursiveSearch) {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry) && recursiveSearch) {
                        scanDir(entry, true);
                        continue;
                    }

                    if (CodeTreeParserFactory.accepts(entry)) {
                        addFile(entry);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", e.getMessage());
        }
    }

    protected void addFile(Path path) {
        files.add(path);
    }

    @Override
    public boolean hasNext() {
        return cursor < files.size();
    }

    @Override
    public Path next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return files.get(cursor++);
    }
}
