package org.qmul.csar.io;

import org.qmul.csar.code.CodeParserFactory;
import org.qmul.csar.util.FilterableIterator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public final class ProjectIteratorFactory {

    /**
     * Returns a new instance of a {@link Iterator<Path>} for the provided arguments. This results will be filtered
     * according to {@link CodeParserFactory#accepts(Path)}, and may be further filtered if the directory is a
     * supported version control system repository (currently on Git repos are handled).
     * <p>
     * If an exception occurs while calling {@link ProjectIterator#init()} for a given iterator, an instance of
     * {@link ProjectIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a git repository, then if only files in the git
     *         repository should be searched
     * @return an instance of {@link Iterator<Path>} for the argument directory
     * @see CodeParserFactory#accepts(Path)
     */
    public static Iterator<Path> create(Path directory, boolean narrowSearch) {
        boolean gitRepository = Files.isDirectory(Paths.get(directory.toString(), ".git"));
        ProjectIterator it;

        try {
            if (gitRepository && narrowSearch) {
                it = new GitProjectIterator(directory);
                it.init();
            } else {
                it = new ProjectIterator(directory);
                it.init();
            }
        } catch (RuntimeException ex) { // fall-back to regular iterator
            it = new ProjectIterator(directory);
            it.init();
        }
        return new FilterableIterator<>(it, CodeParserFactory::accepts);
    }
}
