package org.qmul.csar.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A specialized {@link java.nio.file.FileVisitor<Path>} which allows recursively iteration to be toggled, and
 * aggregates files according to a given {@link Predicate<Path>}. If a file visit is failed an error message is printed
 * to {@link System#err}.
 *
 * @see SimpleFileVisitor
 */
public class ProjectFileVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> files = new ArrayList<>();
    private final boolean recursive;
    private final Predicate<Path> acceptor;

    /**
     * Creates a new ProjectFileVisitor.
     *
     * @param recursive if directories should be recursively iterated
     * @param acceptor the predicate which determines which files are aggregated
     */
    public ProjectFileVisitor(boolean recursive, Predicate<Path> acceptor) {
        this.recursive = recursive;
        this.acceptor = acceptor;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (acceptor.test(file)) {
            files.add(file);
        }
        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException ex) throws IOException {
        System.err.println("Error accessing file: " + file.toString() + " because " + ex.getMessage());
        return super.visitFileFailed(file, ex);
    }

    public List<Path> getFiles() {
        return files;
    }
}
