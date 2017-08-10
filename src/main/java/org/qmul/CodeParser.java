package org.qmul;

import org.qmul.io.ProjectFileScanner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded code file parser.
 */
public final class CodeParser {

    private final ExecutorService executor;
    private final List<ProjectFileScanner.CodeFile> codeFiles;
    private final int threads;
    private int currentCodeFileIdx = 0;

    public CodeParser(List<ProjectFileScanner.CodeFile> codeFiles) {
        this(codeFiles, 1);
    }

    public CodeParser(List<ProjectFileScanner.CodeFile> codeFiles, int threads) {
        this.codeFiles = codeFiles;
        this.threads = threads;
        if (threads <= 0)
            throw new IllegalArgumentException("threads must be greater than 0");
        executor = Executors.newFixedThreadPool(threads);
    }

    public void parse() {
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        ProjectFileScanner.CodeFile file = nextCodeFile();
                        // TODO parse file
                        System.out.println(Thread.currentThread().getName() + ": parsed "
                                + file.getPath().getFileName().toString());
                    }
                } catch (IllegalStateException ex) {
                    System.out.println(Thread.currentThread().getName() + ": done");
                }
            });
        }
    }

    private synchronized ProjectFileScanner.CodeFile nextCodeFile() throws IllegalStateException {
        if (currentCodeFileIdx < codeFiles.size()) {
            return codeFiles.get(currentCodeFileIdx++);
        }
        throw new IllegalStateException("ran out of code files");
    }
}
