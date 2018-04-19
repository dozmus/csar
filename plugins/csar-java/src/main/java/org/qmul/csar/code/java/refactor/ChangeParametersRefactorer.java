package org.qmul.csar.code.java.refactor;

import org.apache.commons.lang3.StringUtils;
import org.qmul.csar.code.RefactorChange;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.parse.statement.ParameterVariableStatement;
import org.qmul.csar.code.java.postprocess.util.TypeHelper;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.result.Result;
import org.qmul.csar.util.FilePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChangeParametersRefactorer implements Refactorer {

    // TODO use typehierarchy on types in here
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeParametersRefactorer.class);
    private final boolean writeToFiles;
    private final List<ParameterVariableDescriptor> descriptors;

    public ChangeParametersRefactorer(List<ParameterVariableDescriptor> descriptors, boolean writeToFiles) {
        this.descriptors = descriptors;
        this.writeToFiles = writeToFiles;
    }

    @Override
    public List<Result> refactor(Path file, List<RefactorChange> changes) throws IOException {
        // Sort changes to prevent indexes from being out of sync
        changes.sort(new RenameComparator());

        // Prepare to make changes
        List<String> lines = Files.readAllLines(file);
        List<Result> results = new ArrayList<>();
        LOGGER.info("==============================================================");
        LOGGER.info("File: {}", file);

        // Modify file
        changes.forEach(r -> {
            String result = changeParameters(lines, descriptors, r);
            results.add(new Result(file, r.lineNumber(), result));
        });

        // Write file
        if (writeToFiles)
            Files.write(file, lines);
        return results;
    }

    /**
     * Applys change parameters to the arguments, for the argument refactor change.
     * This may modify the conventions of the source code.
     */
    private static String changeParameters(List<String> lines, List<ParameterVariableDescriptor> descriptors,
            RefactorChange r) {
        LOGGER.info("changeParameters(lines, decriptors, {})", r.getClass().getSimpleName());
        List<String> newArgs = new ArrayList<>();
        List<TypeInstance> args;
        FilePosition lParen;
        FilePosition rParen;
        List<FilePosition> commas;

        if (r instanceof MethodCallExpressionChangeParametersRefactorChange) {
            MethodCallExpression mce = ((MethodCallExpressionChangeParametersRefactorChange)r).getExpression();
            args = mce.getArgumentTypes();
            lParen = mce.getLeftParenthesisPosition();
            rParen = mce.getRightParenthesisPosition();
            commas = mce.getCommaFilePositions();

            // Modify existing args
            for (int i = 0; i < args.size() && i < descriptors.size(); i++) {
                TypeInstance arg = args.get(i);
                ParameterVariableDescriptor newDescriptor = descriptors.get(i);
                String type = arg.getType() + TypeHelper.dimensionsToString(arg.getDimensions());
                String newType = newDescriptor.getIdentifierType().get();
                LOGGER.info("modify existing args ({}): '{}' vs '{}'", i, type, newType);

                if (!type.equals(newType)) { // replace
                    newArgs.add(newDescriptor.getIdentifierName().get().toString());
                } else { // no change
                    newArgs.add(null);
                }
            }

            // Add args at the end, if necessary
            if (descriptors.size() > args.size()) {
                for (int i = args.size(); i < descriptors.size(); i++) {
                    ParameterVariableDescriptor newDescriptor = descriptors.get(i);
                    newArgs.add(newDescriptor.getIdentifierName().get().toString());
                    LOGGER.info("add more ({}): '{}'", i, newDescriptor.getIdentifierName().get().toString());
                }
            }
        } else if (r instanceof MethodStatementChangeParametersRefactorChange) {
            MethodStatement ms = ((MethodStatementChangeParametersRefactorChange)r).getStatement();
            args = ms.getParameters().stream()
                    .map(ParameterVariableStatement::getTypeInstance)
                    .collect(Collectors.toList());
            lParen = ms.getLeftParenFilePosition();
            rParen = ms.getRightParenFilePosition();
            commas = ms.getCommaFilePositions();

            // Modify existing args
            for (int i = 0; i < args.size() && i < descriptors.size(); i++) {
                TypeInstance arg = args.get(i);
                ParameterVariableDescriptor newDescriptor = descriptors.get(i);
                String type = arg.getType() + TypeHelper.dimensionsToString(arg.getDimensions());
                String identifier = ms.getParameters().get(i).getDescriptor().getIdentifierName().toString();
                String newType = newDescriptor.getIdentifierType().get();
                String newIdentifier = newDescriptor.getIdentifierName().get().toString();
                LOGGER.info("modify existing args ({}): '{}' vs '{}'", i, type, newType);

                if (!type.equals(newType) || !identifier.equals(newIdentifier)) { // replace
                    newArgs.add(newType + " " + newIdentifier);
                } else { // no change
                    newArgs.add(null);
                }
            }

            // Add args at the end, if necessary
            if (descriptors.size() > args.size()) {
                for (int i = args.size(); i < descriptors.size(); i++) {
                    ParameterVariableDescriptor newDescriptor = descriptors.get(i);
                    newArgs.add(newDescriptor.getIdentifierType().get() + " "
                            + newDescriptor.getIdentifierName().get().toString());
                    LOGGER.info("add more ({}): '{}'", i, newDescriptor.getIdentifierType().get() + " "
                            + newDescriptor.getIdentifierName().get().toString());
                }
            }
        } else {
            throw new IllegalArgumentException("invalid RefactorChange type: " + r.getClass().toString());
        }
        LOGGER.info("newArgs.size() = {}", newArgs.size());

        // Apply changes
        // Remove all
        if (newArgs.size() == 0) { // this is handled in the next if statement
            newArgs.add("");
        }

        // Single argument
        if (newArgs.size() == 1) {
            String arg = newArgs.get(0);

            if (arg == null) { // leave first item
                if (commas.size() > 0) { // need to remove rest of arguments
                    FilePosition firstComma = commas.get(0);
                    setLines(lines, firstComma, rParen, "");
                }
            } else {
                setLines(lines, lParen.add(0, 1), rParen, arg);
            }
        }

        // Modify existent arguments, perform changes in reverse order
        if (newArgs.size() > 1) {
            FilePosition firstComma = commas.get(0);
            FilePosition lastComma = commas.get(commas.size() - 1);

            for (int i = Math.min(args.size(), newArgs.size()) - 1; i >= 0; i--) {
                String currentArg = newArgs.get(i);

                if (currentArg == null) // no change needed
                    continue;

                if (i > 0) // conventions
                    currentArg = " " + currentArg;

                if (i == args.size() - 1) { // last param
                    setLines(lines, lastComma.add(0, 1), rParen, currentArg);
                    int rParenIdx = lastComma.getColumnNumber() + currentArg.length() + 1;
                    rParen = new FilePosition(lastComma.getLineNumber(), rParenIdx); // update rParen
                } else if (i == 0) { // first param
                    setLines(lines, lParen.add(0, 1), firstComma, currentArg);
                } else { // middle param
                    FilePosition prevComma = commas.get(i);
                    FilePosition nextComma = commas.get(i + 1);
                    setLines(lines, prevComma.add(0, 1), nextComma, currentArg);
                }
            }
        }

        // Remove any extra unwanted args
        if (newArgs.size() < args.size()) {
            for (int i = args.size() - 1; i > newArgs.size(); i--) {
                FilePosition prevComma = commas.get(i);
                FilePosition nextComma = commas.get(i + 1);
                setLines(lines, prevComma.add(0, 1), nextComma, "");
            }
        }

        String newCode = "";
        LOGGER.info("lines: {}", lines.toString());

        try {
            // Add any extra wanted args
            if (newArgs.size() > args.size()) {
                for (int i = args.size(); i < newArgs.size(); i++) {
                    String currentArg = ", " + newArgs.get(i);
                    LOGGER.info("Insert({}): {}", i, currentArg);
                    insertLine(lines, rParen, currentArg);
                    rParen = rParen.add(0, currentArg.length());
                }
            }
        } catch (Exception e) {
            LOGGER.info("Ex:", e);
        }

        // TODO check if change was made
        newCode = substring(lines, lParen.getLineNumber() - 1, rParen.getLineNumber() - 1);
        newCode = StringUtils.stripEnd(newCode, "\r\n ");
        LOGGER.info("ChangeParameters: ... => {}", newCode);
        return newCode;
    }

    /**
     * Returns a substring from the argument lines, starting with the start index on the start line, and ending with
     * the end index on the end line.
     * All indices are inclusive.
     */
    private static String substring(List<String> lines, int startLine, int startIdx, int endLine, int endIdx) {
        if (endLine < startLine || startLine == endLine && endIdx < startIdx)
            throw new IllegalArgumentException("indices overlap");

        if (startLine == endLine) {
            return lines.get(startLine).substring(startIdx, endIdx + 1);
        } else {
            StringBuilder substring = new StringBuilder();

            for (int i = startLine; i <= endLine; i++) {
                if (i == startLine) {
                    substring.append(lines.get(i).substring(startIdx)).append(lineSeparator());
                } else if (i == endLine) {
                    substring.append(lines.get(i).substring(0, endIdx + 1));
                } else {
                    substring.append(lines.get(i)).append(lineSeparator());
                }
            }
            return substring.toString();
        }
    }

    /**
     * Returns a substring from the argument lines, starting with the start index on the start line, and ending with
     * the end index on the end line.
     * All indices are inclusive.
     */
    private static String substring(List<String> lines, int startLine, int endLine) {
        if (endLine < startLine)
            throw new IllegalArgumentException("indices overlap");

        if (startLine == endLine) {
            return lines.get(startLine);
        } else {
            StringBuilder substring = new StringBuilder();

            for (int i = startLine; i <= endLine; i++) {
                substring.append(lines.get(i));

                if (i + 1 > endLine)
                    substring.append(lineSeparator());
            }
            return substring.toString();
        }
    }

    /**
     * Returns a substring from the argument lines, starting with the start position and ending with the end position.
     * All indices are inclusive.
     */
    private static String substring(List<String> lines, FilePosition startPosition, FilePosition endPosition) {
        return substring(lines, startPosition.getLineNumber() - 1, startPosition.getColumnNumber(),
                endPosition.getLineNumber() - 1, endPosition.getColumnNumber());
    }

    /**
     * Sets the content of the argument lines, starting with the start index on the start line, and ending with the end
     * index on the end line.
     * All indices are inclusive.
     *
     * @param padLinesToRemove if lines which should be deleted, should be left blank instead
     */
    public static void setLines(List<String> lines, int startLine, int startIdx, int endLine, int endIdx,
            String content, boolean padLinesToRemove) {
        if (endLine < startLine || startLine == endLine && endIdx < startIdx)
            throw new IllegalArgumentException("indices overlap");
        String start = lines.get(startLine).substring(0, startIdx);
        String end = lines.get(endLine).substring(endIdx);
        int regionLineAmount = endLine - startLine + 1;

        // Generate contents
        String[] contents = content.split(lineSeparator());

        if (regionLineAmount < contents.length)
            throw new IllegalArgumentException("contents array too big to fit in region");

        // Set contents
        List<Integer> removalIndices = new ArrayList<>();

        if (contents.length == 1) {
            lines.set(startLine, start + contents[0] + end);

            if (regionLineAmount > 1) {
                removalIndices = IntStream.range(startLine + 1, endLine + 1).boxed().collect(Collectors.toList());
            }
        } else {
            for (int contentIdx = 0; contentIdx < contents.length; contentIdx++) {
                int lineNo = startLine + contentIdx;

                if (contentIdx == 0) {
                    lines.set(lineNo, start + contents[contentIdx]);
                } else if (contentIdx + 1 >= contents.length && lineNo < endLine) {
                    removalIndices.add(lineNo);
                } else if (lineNo == endLine) {
                    lines.set(lineNo, contents[contentIdx] + end);
                } else {
                    lines.set(lineNo, contents[contentIdx]);
                }
            }
        }

        // Remove lines
        removalIndices.sort(Comparator.reverseOrder());

        for (int i : removalIndices) {
            if (padLinesToRemove)
                lines.set(i, "");
            else
                lines.remove(i);
        }
    }

    /**
     * Sets the content of the argument lines, starting with the start position and ending with the end position.
     * All indices are inclusive.
     */
    public static void setLines(List<String> lines, FilePosition startPosition, FilePosition endPosition,
            String content) {
        setLines(lines, startPosition.getLineNumber() - 1, startPosition.getColumnNumber(),
                endPosition.getLineNumber() - 1, endPosition.getColumnNumber(), content, true);
    }

    /**
     * Sets the content of the argument line, by inserting the content into the argument position.
     */
    public static void insertLine(List<String> lines, FilePosition startPosition, String content) {
        int lineIdx = startPosition.getLineNumber() - 1;
        String line = lines.get(lineIdx);
        int maxColumn = Math.min(line.length(), startPosition.getColumnNumber());
        String start = line.substring(0, maxColumn);
        String end = start.equals(line) ? "" : line.substring(maxColumn);
        LOGGER.info("({},{}) => {}|{}|{}", lineIdx + 1, startPosition.getColumnNumber(), start, content, end);
        lines.set(lineIdx, start + content + end);
    }

    /**
     * Sets the content of the argument lines, starting with the start position and ending with the end position.
     * All indices are inclusive.
     *
     * @param padLinesToRemove if lines which should be deleted, should be left blank instead
     */
    public static void setLines(List<String> lines, FilePosition startPosition, FilePosition endPosition,
            String content, boolean padLinesToRemove) {
        setLines(lines, startPosition.getLineNumber() - 1, startPosition.getColumnNumber(),
                endPosition.getLineNumber() - 1, endPosition.getColumnNumber(), content, padLinesToRemove);
    }

    private static String lineSeparator() {
        return System.getProperty("line.separator");
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
