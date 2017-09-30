package org.qmul.csar.io;

import com.purecs.ignorefiles.IgnoreFiles;
import com.purecs.ignorefiles.Rule;
import org.qmul.csar.code.CodeParserFactory;
import org.qmul.csar.util.FilterableIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class ProjectIteratorFactory {

    /**
     * Returns a new {@link ProjectIterator} for the provided arguments.
     * <p>
     * This results will be filtered if the directory is a supported version control system repository
     * (currently supported: Git) if <tt>narrowSearch</tt> is <tt>true</tt>.
     * <p>
     * If an exception occurs while calling {@link ProjectIterator#init()} for a given iterator, an instance of
     * {@link ProjectIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @return an instance of {@link ProjectIterator} for the argument directory
     */
    public static Iterator<Path> createProjectIterator(Path directory, boolean narrowSearch) {
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
        return it;
    }

    /**
     * Returns a new {@link FilterableIterator} for the provided arguments, whose results are filtered by
     * {@link CodeParserFactory#accepts(Path)}.
     * <p>
     * This results will be filtered further if <tt>narrowSearch</tt> is <tt>true</tt> and if the directory is a
     * supported version control system repository (currently supported: Git).
     * <p>
     * If an exception occurs while calling {@link ProjectIterator#init()} for a given iterator, an instance of
     * {@link ProjectIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @return an instance of {@link FilterableIterator} for the argument directory
     * @see CodeParserFactory#accepts(Path)
     */
    public static Iterator<Path> createFilteredIterator(Path directory, boolean narrowSearch) {
        Iterator<Path> it = createProjectIterator(directory, narrowSearch);
        return new FilterableIterator<>(it, CodeParserFactory::accepts);
    }

    /**
     * Returns a new {@link FilterableIterator} for the provided arguments, whose results are filtered by
     * {@link CodeParserFactory#accepts(Path)} and {@link IgnoreFiles#ignored(Path, List)}. The parsed rules
     * have their base directory set to the parent of <tt>ignoreFile</tt>.
     * <p>
     * This results will be filtered further if <tt>narrowSearch</tt> is <tt>true</tt> and if the directory is a
     * supported version control system repository (currently supported: Git).
     * <p>
     * If an exception occurs while calling {@link ProjectIterator#init()} for a given iterator, an instance of
     * {@link ProjectIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @param ignoreFile the ignore file to parse rules from
     * @return an instance of {@link FilterableIterator} for the argument directory
     * @throws IOException if an I/O error occurs
     * @see CodeParserFactory#accepts(Path)
     * @see IgnoreFiles#ignored(Path, List)
     */
    public static Iterator<Path> createFilteredIterator(Path directory, boolean narrowSearch, Path ignoreFile)
            throws IOException {
        Iterator<Path> it = createProjectIterator(directory, narrowSearch);
        List<Rule> rules = IgnoreFiles.read(ignoreFile.toAbsolutePath().getParent().toString(), ignoreFile);

        // Create and return iterator
        Predicate<Path> acceptor = path -> !IgnoreFiles.ignored(path, rules) && CodeParserFactory.accepts(path);
        return new FilterableIterator<>(it, acceptor);
    }
}
