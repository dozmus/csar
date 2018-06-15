package org.qmul.csar.io.it.vcs;

import org.qmul.csar.io.it.ProjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A Version Control Software repository iterator.
 *
 * @see ProjectIterator
 */
public abstract class VCSRepositoryIterator extends ProjectIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryIterator.class);
    private final String vcsName;
    private final String vcsFolderName;

    /**
     * Creates a new instance with the argument directory, VCS name and reserved folder name.
     *
     * @param directory the directory to search for files in
     * @param vcsName the name of the VCS supported
     * @param vcsFolderName the name of the repository's folder
     */
    public VCSRepositoryIterator(Path directory, String vcsName, String vcsFolderName) {
        super(directory);
        this.vcsName = vcsName;
        this.vcsFolderName = vcsFolderName;
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", getDirectory().toString());
        boolean isValid = Files.isDirectory(Paths.get(getDirectory().toString(), vcsFolderName));

        if (!isValid)
            throw new IllegalStateException("repository is missing a " + vcsFolderName + " folder");
        scanVcsDir();
        initialized = true;
    }

    /**
     * Finds code files in a VCS repository, which are in the staging area (added or modified) or have been committed.
     *
     * @throws RuntimeException if an error occurs
     * @see #lsFiles()
     */
    private void scanVcsDir() {
        LOGGER.trace("Scanning " + vcsName + " repository");

        try {
            List<Path> output = lsFiles();
            output.forEach(this::addFile);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException("unable to scan " + vcsName + " directory: " + ex.getMessage());
        }
    }

    /**
     * Returns a list of the files which are in the staging area (added or modified) or have been committed.
     *
     * @return a list of the output paths
     * @throws Exception if an error occurs
     */
    protected abstract List<Path> lsFiles() throws Exception;
}
