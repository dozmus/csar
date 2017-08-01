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
import java.util.List;

/**
 * Finds code files in a directory.
 */
public final class ProjectFileScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectFileScanner.class);
    private final CsarContext ctx;

    public ProjectFileScanner(CsarContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Finds the code files in the working directory and adds them to {@link CsarContext#codeFiles}.
     */
    public void scan() {
        LOGGER.info("Scanning project directory: {}", ctx.getDirectory().toString());

        // Find files
        if (ctx.isGitRepository()) {
            scanGitDir();
        } else {
            scanDir();
        }

        // Sort code files by size
        ctx.getCodeFiles().sort((f1, f2) -> (int) (f1.getSize() - f2.getSize()));
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

            if (ctx.accepts(path)) {
                ctx.getCodeFiles().add(new CodeFile(path));
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
                    }

                    if (ctx.accepts(entry)) {
                        ctx.getCodeFiles().add(new CodeFile(entry));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", e.getMessage());
        }
    }

    /**
     * A code file on a storage device.
     */
    public static final class CodeFile {

        private final Path path;
        private final long size;

        public CodeFile(Path path) {
            this.path = path;
            long size = -1;

            try {
                size = Files.size(path);
            } catch (IOException ignored) {
            }
            this.size = size;
        }

        public Path getPath() {
            return path;
        }

        public long getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "CodeFile{path=" + path + ", size=" + size + "}";
        }
    }
}
