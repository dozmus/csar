package org.qmul.csar.io.it;

import com.github.dozmus.iterators.DirectoryFileIterator;
import com.github.dozmus.iterators.FilterableIterator;
import com.github.dozmus.iterators.ignorefile.IgnoreFile;
import com.github.dozmus.iterators.ignorefile.Rule;
import com.github.dozmus.iterators.vcs.GitRepositoryIterator;
import com.github.dozmus.iterators.vcs.MercurialRepositoryIterator;
import com.github.dozmus.iterators.vcs.SubversionRepositoryIterator;
import org.qmul.csar.code.parse.CodeParserFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class ProjectIteratorFactory {

    /**
     * Returns a new {@link DirectoryFileIterator} for the provided arguments.
     * <p>
     * This results will be filtered if the directory is a supported version control system repository
     * (currently supported: Git) if <tt>narrowSearch</tt> is <tt>true</tt>.
     * <p>
     * If an exception occurs while calling {@link DirectoryFileIterator#init()} for a given iterator, an instance of
     * {@link DirectoryFileIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @return an instance of {@link DirectoryFileIterator} for the argument directory
     */
    public static Iterator<Path> createProjectIterator(Path directory, boolean narrowSearch) {
        boolean gitRepository = Files.isDirectory(Paths.get(directory.toString(), ".git"));
        boolean svnRepository = Files.isDirectory(Paths.get(directory.toString(), ".svn"));
        boolean hgRepository = Files.isDirectory(Paths.get(directory.toString(), ".hg"));
        DirectoryFileIterator it = null;

        try {
            if (narrowSearch) {
                if (gitRepository) {
                    it = new GitRepositoryIterator(directory);
                    it.init();
                } else if (svnRepository) {
                    it = new SubversionRepositoryIterator(directory);
                    it.init();
                } else if (hgRepository) {
                    it = new MercurialRepositoryIterator(directory);
                    it.init();
                }
            }
        } catch (RuntimeException ignored) {
        }

        // if it is invalid then fall-back to regular iterator
        if (it == null) {
            it = new DirectoryFileIterator(directory, true);
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
     * If an exception occurs while calling {@link DirectoryFileIterator#init()} for a given iterator, an instance of
     * {@link DirectoryFileIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @return an instance of {@link FilterableIterator} for the argument directory
     * @see CodeParserFactory#accepts(Path)
     */
    public static Iterator<Path> createFilteredIterator(Path directory, boolean narrowSearch,
            CodeParserFactory factory) {
        Iterator<Path> it = createProjectIterator(directory, narrowSearch);
        return new FilterableIterator<>(it, factory::accepts);
    }

    /**
     * Returns a new {@link FilterableIterator} for the provided arguments, whose results are filtered by
     * {@link CodeParserFactory#accepts(Path)} and {@link IgnoreFile#ignored(Path, List)}. The parsed rules
     * have their base directory set to the parent of <tt>ignoreFile</tt>.
     * <p>
     * This results will be filtered further if <tt>narrowSearch</tt> is <tt>true</tt> and if the directory is a
     * supported version control system repository (currently supported: Git).
     * <p>
     * If an exception occurs while calling {@link DirectoryFileIterator#init()} for a given iterator, an instance of
     * {@link DirectoryFileIterator} will be returned instead which will recursively iterate the entire argument directory.
     *
     * @param directory the directory to search for files in
     * @param narrowSearch if the directory is home to a supported version control repository
     * @param ignoreFile the ignore file to parse rules from
     * @return an instance of {@link FilterableIterator} for the argument directory
     * @throws IOException if an I/O error occurs
     * @see CodeParserFactory#accepts(Path)
     * @see IgnoreFile#ignored(Path, List)
     */
    public static Iterator<Path> createFilteredIterator(Path directory, boolean narrowSearch, Path ignoreFile,
            CodeParserFactory factory)
            throws IOException {
        Iterator<Path> it = createProjectIterator(directory, narrowSearch);
        List<Rule> rules = IgnoreFile.read(ignoreFile.toAbsolutePath().getParent().toString(), ignoreFile);

        // Create and return iterator
        Predicate<Path> acceptor = path -> !IgnoreFile.ignored(path, rules) && factory.accepts(path);
        return new FilterableIterator<>(it, acceptor);
    }
}
