package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.code.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A renaming refactorer.
 */
public class RenameRefactorer implements Refactorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameRefactorer.class);
    private final String newName;
    private final boolean writeToFiles;

    public RenameRefactorer(String newName, boolean writeToFiles) {
        this.newName = newName;
        this.writeToFiles = writeToFiles;
    }

    @Override
    public List<Result> refactor(Path file, List<RefactorChange> changes) throws IOException {
        // Sort changes to prevent indexes from being out of sync
        changes.sort(new RenameComparator());

        // Prepare to make changes
        List<String> lines = Files.readAllLines(file);
        List<Result> results = new ArrayList<>();

        // Modify file
        changes.forEach(r -> {
            String result = rename(lines, newName, r);
            results.add(new Result(file, r.lineNumber(), result));
        });

        // Write file
        if (writeToFiles)
            Files.write(file, lines);
        return results;
    }

    private static String rename(List<String> lines, String newName, RefactorChange r) {
        int lineNo = r.lineNumber() - 1;
        int startIdx = r.startIndex();
        int endIdx = r.endIndex();

        String code = lines.get(lineNo);
        String p1 = code.substring(0, startIdx);
        String p2 = code.substring(endIdx);
        String newCode = p1 + newName + p2;
        lines.set(lineNo, newCode);
        LOGGER.trace("rename: ({},{},{}) | {} => {}", lineNo, startIdx, endIdx, code, newCode);
        return newCode;
    }

    /**
     * A comparator for renaming refactoring. This sorts in descending order using the identifier name's start index.
     */
    private static final class RenameComparator implements Comparator<RefactorChange> {

        @Override
        public int compare(RefactorChange o1, RefactorChange o2) {
            return o2.startIndex() - o1.startIndex();
        }
    }
}
