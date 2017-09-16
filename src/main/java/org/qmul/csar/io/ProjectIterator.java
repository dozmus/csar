package org.qmul.csar.io;

import org.qmul.csar.code.CodeTreeParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates over files in the specified directory aggregating accepted ones. The code files may be narrowed down further
 * if the folder is a git repository (if successful).
 *
 * @see CodeTreeParserFactory#accepts(Path)
 * @see #scanGitDir()
 */
public class ProjectIterator implements Iterator<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIterator.class);
    private final boolean narrowSearch;
    private final Path directory;
    private List<Path> files = new ArrayList<>(); // the element collection
    private int cursor = 0; // the index of the next element to return
    private boolean initialized;

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
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a git repository, then if only files in the git
     *         repository should be searched
     */
    public ProjectIterator(Path directory, boolean narrowSearch) {
        this.directory = directory;
        this.narrowSearch = narrowSearch;
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    private void init() {
        LOGGER.info("Scanning project directory: {}", directory.toString());
        boolean gitRepository = Files.isDirectory(Paths.get(directory.toString(), ".git"));

        // Find files
        if (gitRepository && narrowSearch) {
            scanGitDir();
        } else {
            scanDir();
        }
        initialized = true;
    }

    /**
     * Finds code files in a git repository, which are in the staging area or have been committed. This is done by
     * creating an instance of the git program. Failure will result in {@link #scanDir()} being called instead.
     *
     * @see <a href="https://git-scm.com/docs/git-ls-files">git ls-files</a>
     * @see CodeTreeParserFactory#accepts(Path)
     * @see GitProcessHelper#lsFiles()
     */
    private void scanGitDir() {
        LOGGER.trace("Scanning git repository");

        try {
            List<Path> output = GitProcessHelper.lsFiles();

            for (Path path : output) {
                if (CodeTreeParserFactory.accepts(path)) {
                    addFile(path);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            scanDir();
        }
    }

    /**
     * Calls {@link #scanDir(Path, boolean)} with arguments {@link #directory} and <tt>true</tt> respectively.
     */
    private void scanDir() {
        scanDir(directory, true);
    }

    /**
     * Scans the specified directory using {@link ProjectFileVisitor}. The results are then added to {@link #files}.
     * Symbolic links are not followed.
     *
     * @param path the directory to be searched
     * @param recursive if the directory should be searched recursively
     * @see CodeTreeParserFactory#accepts(Path)
     * @see ProjectFileVisitor
     */
    private void scanDir(Path path, boolean recursive) {
        try {
            ProjectFileVisitor fileVisitor = new ProjectFileVisitor(recursive, CodeTreeParserFactory::accepts);
            Files.walkFileTree(path, fileVisitor);
            files.addAll(fileVisitor.getFiles());
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", e.getMessage());
        }
    }

    protected void addFile(Path path) {
        files.add(path);
    }

    @Override
    public boolean hasNext() {
        if (!initialized)
            init();
        return cursor < files.size();
    }

    @Override
    public Path next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return files.get(cursor++);
    }
}
