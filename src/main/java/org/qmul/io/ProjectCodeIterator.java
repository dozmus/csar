package org.qmul.io;

import org.qmul.CsarContext;
import org.qmul.util.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates over accepted code files in a directory.
 * @see {@link CsarContext#accepts(Path)}
 */
public final class ProjectCodeIterator implements Iterator<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCodeIterator.class);
    private final CsarContext ctx;
    private final List<Path> files = new ArrayList<>();
    private int currentIdx = 0;

    public ProjectCodeIterator(CsarContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Finds the code files in the working directory and stores them in {@link #files}.
     */
    public void init() {
        LOGGER.info("Scanning project directory: {}", ctx.getDirectory().toString());
        files.clear();
        currentIdx = 0;

        // Find files
        if (ctx.isGitRepository()) {
            scanGitDir();
        } else {
            scanDir();
        }
    }

    /**
     * Finds code files in a git repository, which are in the staging area or have been committed. This is done by
     * creating an instance of the git program. Failure will result in {@link #scanDir()} being called instead.
     * @see <a href="https://git-scm.com/docs/git-ls-files">git ls-files</a>
     */
    private void scanGitDir() {
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
        String output1 = output.get(0);

        if (output1.startsWith("fatal: Not a git repository")
                || output1.startsWith("'git' is not recognized as an internal or external command")) {
            LOGGER.error("Error running git ls-files: {}", output1);
            scanDir();
            return;
        }

        for (String fileName : output) {
            Path path = Paths.get(fileName);

            if (ctx.accepts(path) && !Files.isDirectory(path)) {
                files.add(path);
            }
        }
    }

    private void scanDir() {
        scanDir(ctx.getDirectory());
    }

    private void scanDir(Path path) {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        scanDir(entry);
                        continue;
                    }

                    if (ctx.accepts(entry)) {
                        files.add(entry);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", e.getMessage());
        }
    }

    @Override
    public boolean hasNext() {
        return currentIdx < files.size();
    }

    @Override
    public Path next() {
        return files.get(currentIdx++);
    }
}
