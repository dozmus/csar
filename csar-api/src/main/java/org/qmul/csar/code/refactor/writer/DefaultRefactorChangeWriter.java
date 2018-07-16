package org.qmul.csar.code.refactor.writer;

import org.qmul.csar.code.Result;
import org.qmul.csar.code.refactor.RefactorChange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A thread-safe refactor change writer.
 * This writes the changes described by refactor changes to their files.
 * This will fail if any two changes overlap.
 */
public class DefaultRefactorChangeWriter implements RefactorChangeWriter {

    /**
     * Writes refactor changes to their corresponding files, and returns their corresponding results.
     * This will not return multiple results for the same line.
     *
     * This will perform the changes in reverse-order, to prevent having to update the indices of subsequent changes.
     *
     * @param changes all of the changes for a specific file
     */
    public List<Result> writeAll(List<RefactorChange> changes) throws IOException {
        Path path = changes.get(0).getPath();
        String contents = new String(Files.readAllBytes(path));

        // Iterate changes in reverse order
        changes.sort((c1, c2) -> {
            int k = c2.getEndOffset() - c1.getEndOffset();
            return (k != 0) ? k : c2.getStartOffset() - c1.getStartOffset();
        });

        // Apply changes
        for (RefactorChange change : changes) {
            contents = apply(contents, change);
        }

        // Write changes
        write(path, contents);

        // Create results, this is done afterwards in case the same line is modified multiple times
        return results(contents, path, changes);
    }

    /**
     * Returns the results corresponding to the argument changes.
     */
    private List<Result> results(String contents, Path path, List<RefactorChange> changes) {
        final String[] lines = contents.split(System.lineSeparator());
        return changes.stream()
                .map(r -> new Result(path, r.getLineNumber(), lines[r.getLineNumber() - 1]))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Writes the argument <tt>String</tt> to the argument path.
     */
    public void write(Path path, String contents) throws IOException {
        Files.write(path, contents.getBytes());
    }

    /**
     * Returns a copy of the argument <tt>String</tt>, with the argument change applied to it.
     */
    public String apply(String src, RefactorChange change) {
        if (change.getStartOffset() == 0) {
            return change.getReplacement() + src.substring(change.getEndOffset());
        } else if (change.getEndOffset() == src.length()) {
            return change.getReplacement();
        } else {
            return src.substring(0, change.getStartOffset()) + change.getReplacement()
                    + src.substring(change.getEndOffset());
        }
    }
}
