package org.qmul.csar.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Recursively iterates over files in the specified directory aggregating them.
 */
public class ProjectIterator implements Iterator<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIterator.class);
    private final Path directory;
    private final List<Path> files = new ArrayList<>(); // the element collection
    private int cursor = 0; // the index of the next element to return
    protected boolean initialized;

    /**
     * Creates a new instance with the argument directory.
     *
     * @param directory the directory to search for files in
     */
    public ProjectIterator(Path directory) {
        this.directory = directory;
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {} ({})", directory.toString(), directory.toAbsolutePath().toString());
        scanDir();
        initialized = true;
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
     * @see ProjectFileVisitor
     */
    private void scanDir(Path path, boolean recursive) {
        try {
            ProjectFileVisitor visitor = new ProjectFileVisitor(recursive);
            Files.walkFileTree(path, visitor);
            files.addAll(visitor.getFiles());
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", e.getMessage());
        }
    }

    protected void addFile(Path path) {
        files.add(path);
    }

    public Path getDirectory() {
        return directory;
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
