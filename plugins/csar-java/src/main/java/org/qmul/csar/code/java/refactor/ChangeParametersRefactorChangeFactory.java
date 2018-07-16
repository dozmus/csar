package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.util.FilePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeParametersRefactorChangeFactory implements RefactorChangeFactory<List<ParameterVariableDescriptor>> {

    // TODO insert 'final' modifier where appropriate as well?
    // TODO handle varargs replacements, like subtype replacements, in changeMethodCallExpression?
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeParametersRefactorChangeFactory.class);
    private final TypeHierarchyResolver thr;

    public ChangeParametersRefactorChangeFactory(TypeHierarchyResolver thr) {
        this.thr = thr;
    }

    public List<RefactorChange> changes(SerializableCode target, List<ParameterVariableDescriptor> newParameters) {
        if (target instanceof MethodStatement) {
            return changesForMethod((MethodStatement) target, newParameters);
        } else if (target instanceof MethodCallExpression) {
            return changesForMethodCall((MethodCallExpression) target, newParameters);
        }
        throw new IllegalArgumentException("invalid target item");
    }

    /**
     * Returns the refactor changes for refactoring a method statement.
     */
    private static List<RefactorChange> changesForMethod(MethodStatement ms,
            List<ParameterVariableDescriptor> newParameters) {
        LOGGER.trace("Change Parameters (method)");
        List<String> args = ms.getParameters().stream()
                .map(p -> p.getTypeInstance().getTypeWithDimensions() + " " + p.getDescriptor().getIdentifierName().get())
                .collect(Collectors.toList());
        FilePosition lParen = ms.getLeftParenFilePosition();
        FilePosition rParen = ms.getRightParenFilePosition();
        List<FilePosition> commas = ms.getCommaFilePositions();
        int lineNumber = ms.getIdentifierFilePosition().getLineNumber();

        // Create list of new args
        List<String> newArgs = newParameters.stream()
                .map(desc -> desc.getIdentifierType().get() + " " + desc.getIdentifierName().get())
                .collect(Collectors.toList());
        return changes(args, newArgs, lParen, rParen, commas, ms.getPath(), lineNumber);
    }

    /**
     * Returns the refactor changes for refactoring a method call expression.
     */
    private List<RefactorChange> changesForMethodCall(MethodCallExpression mce,
            List<ParameterVariableDescriptor> newParameters) {
        LOGGER.trace("Change Parameters (method call)");
        List<TypeInstance> argsTypes = mce.getArgumentTypes();
        FilePosition lParen = mce.getLeftParenthesisPosition();
        FilePosition rParen = mce.getRightParenthesisPosition();
        List<FilePosition> commas = mce.getCommaFilePositions();
        int lineNumber = mce.getIdentifierFilePosition().getLineNumber();

        // Create list of new args
        List<String> newArgsTypes = newParameters.stream()
                .map(desc -> desc.getIdentifierType().get())
                .collect(Collectors.toList());
        List<String> newArgsIdentifiers = newParameters.stream()
                .map(desc -> desc.getIdentifierName().get().toString())
                .collect(Collectors.toList());
        return mceChanges(argsTypes, newArgsTypes, newArgsIdentifiers, lParen, rParen, commas, mce.getPath(),
                lineNumber);
    }

    private static List<RefactorChange> changes(List<String> args, List<String> newArgs, FilePosition lParen,
            FilePosition rParen, List<FilePosition> commas, Path path, int lineNumber) {
        LOGGER.trace("Change Parameters: old={}, new={}", args, newArgs);
        int oldSize = args.size();
        int newSize = newArgs.size();

        // Create refactor changes
        List<RefactorChange> changes = new ArrayList<>();

        // Change needed if they are not equal (this also includes if they are both empty)
        if (args.equals(newArgs))
            return Collections.emptyList();

        // Replace arguments
        for (int i = Math.min(oldSize, newSize) - 1; i >= 0; i--) {
            String newArg = newArgs.get(i);
            String oldArg = args.get(i);

            // Check if change needed
            if (oldArg.equals(newArg))
                continue;

            // Conventions of new argument
            if (i > 0)
                newArg = " " + newArg;

            // Create refactor change
            RefactorChange change = replaceArgument(oldSize, i, commas, lParen, rParen, path, lineNumber, newArg);
            changes.add(change);
        }

        // Handle size mismatches
        changes.addAll(changesSizeMismatch(oldSize, newArgs, commas, lParen, rParen, path, lineNumber));
        return changes;
    }

    private List<RefactorChange> mceChanges(List<TypeInstance> argsTypeInstances, List<String> newArgsTypes,
            List<String> newArgsIdentifiers, FilePosition lParen, FilePosition rParen, List<FilePosition> commas,
            Path path, int lineNumber) {
        List<String> argsTypes = argsTypeInstances.stream()
                .map(TypeInstance::getTypeWithDimensions)
                .collect(Collectors.toList());
        LOGGER.trace("Change Parameters: old={}, new={}", argsTypes, newArgsTypes);
        int oldSize = argsTypes.size();
        int newSize = newArgsTypes.size();

        // Create refactor changes
        List<RefactorChange> changes = new ArrayList<>();

        // Change needed if they are not equal (this also includes if they are both empty)
        if (equals(argsTypeInstances, newArgsTypes, thr))
            return Collections.emptyList();

        // Replace arguments
        for (int i = Math.min(oldSize, newSize) - 1; i >= 0; i--) {
            String oldArgQualifiedName = argsTypeInstances.get(i).getQualifiedName();
            String newArg = newArgsTypes.get(i);
            String replacement = newArgsIdentifiers.get(i);

            // Check if change needed
            if (thr.isPossiblySubtype(oldArgQualifiedName, newArg))
                continue;

            // Conventions of new argument
            if (i > 0)
                replacement = " " + replacement;

            // Create refactor change
            RefactorChange change = replaceArgument(oldSize, i, commas, lParen, rParen, path, lineNumber, replacement);
            changes.add(change);
        }

        // Account for size mismatches
        changes.addAll(changesSizeMismatch(oldSize, newArgsIdentifiers, commas, lParen, rParen, path, lineNumber));
        return changes;
    }

    private boolean equals(List<TypeInstance> argsTypeInstances, List<String> newArgsTypes, TypeHierarchyResolver thr) {
        if (argsTypeInstances.size() != newArgsTypes.size())
            return false;

        for (int i = 0; i < newArgsTypes.size(); i++) {
            TypeInstance l = argsTypeInstances.get(i);
            String r = newArgsTypes.get(i);

            if (!thr.isPossiblySubtype(l.getQualifiedName(), r))
                return false;
        }
        return true;
    }

    /**
     * Returns the refactor change for an argument replacement.
     * @param i index of argument
     */
    private static RefactorChange replaceArgument(int oldSize, int i, List<FilePosition> commas,
            FilePosition lParen, FilePosition rParen, Path path, int lineNumber, String replacement) {
        FilePosition firstComma;
        FilePosition lastComma;

        if (i == 0 && i == oldSize - 1) { // only param
            return new RefactorChange(path, lineNumber, lParen.getFileOffset() + 1,
                    rParen.getFileOffset(), replacement);
        } else {
            firstComma = commas.get(0);
            lastComma = commas.get(commas.size() - 1);

            if (i == oldSize - 1) { // last param
                return new RefactorChange(path, lineNumber, lastComma.getFileOffset() + 1,
                        rParen.getFileOffset(), replacement);
            } else if (i == 0) { // first param
                return new RefactorChange(path, lineNumber, lParen.getFileOffset() + 1,
                        firstComma.getFileOffset(), replacement);
            } else { // middle param
                FilePosition prevComma = commas.get(i);
                FilePosition nextComma = commas.get(i + 1);
                return new RefactorChange(path, lineNumber, prevComma.getFileOffset() + 1,
                        nextComma.getFileOffset(), replacement);
            }
        }
    }

    /**
     * Returns the refactor changes which account for size mismatches in the two lists of arguments.
     */
    private static List<RefactorChange> changesSizeMismatch(int oldSize, List<String> newArgs,
            List<FilePosition> commas, FilePosition lParen, FilePosition rParen, Path path, int lineNumber) {
        List<RefactorChange> changes = new ArrayList<>();
        int newSize = newArgs.size();

        if (newSize > oldSize) { // Argument additions

            List<String> newPart = newArgs.subList(oldSize, newSize);
            String additionArgsSection = (oldSize == 0 ? "" : ", ") + String.join(", ", newPart);
            changes.add(new RefactorChange(path, lineNumber, rParen.getFileOffset(), rParen.getFileOffset(),
                    additionArgsSection));
        } else if (newSize < oldSize) { // Argument removals

            for (int i = oldSize; i > newSize; i--) {
                FilePosition start = (i == 1) ? lParen : commas.get(i - 2);
                FilePosition end = (i == oldSize) ? rParen : commas.get(i - 1);
                // Remove preceding comma, but not preceding lParen
                int startOffset = start.getFileOffset() + (start.equals(lParen) ? 1 : 0);
                changes.add(new RefactorChange(path, lineNumber, startOffset, end.getFileOffset(), ""));
            }
        }
        return changes;
    }
}
