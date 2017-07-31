package org.qmul.io;

import org.qmul.CsarContext;
import org.qmul.util.ProcessHelper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class ProjectFileScanner {

    private final CsarContext ctx;

    public ProjectFileScanner(CsarContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Finds the code files in the working directory and adds them to {@link CsarContext#codeFiles}.
     */
    public void scan() {
        // find files
        if (ctx.isGitRepository()) { // run: git ls-files
            scanGitDir();
        } else { // scan directory
            scanDir();
        }

        // sort list by size
        ctx.getCodeFiles().sort((cf1, cf2) -> (int) (cf1.getSize() - cf2.getSize()));
    }

    private void scanGitDir() {
        List<String> output;

        try {
            Process p = ProcessHelper.runAndWait("git", "ls-files");
            output = ProcessHelper.readOutput(p);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            scanDir();
            return;
        }


        // Check if git repository found
        String output1 = output.get(0);

        if (output1.startsWith("fatal: Not a git repository")
                || output1.startsWith("'git' is not recognized as an internal or external command")) {
            scanDir();
            return;
        }

        for (String fileName : output) {
            Path path = Paths.get(fileName);

            if (path.getFileName().toString().endsWith(".java")) {
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

                    if (entry.getFileName().toString().endsWith(".java")) {
                        ctx.getCodeFiles().add(new CodeFile(entry));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
