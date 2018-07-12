package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.refactor.change.MethodCallExpressionChangeParametersRefactorChange;
import org.qmul.csar.code.java.refactor.change.MethodCallExpressionIdentifierRefactorChange;
import org.qmul.csar.code.java.refactor.change.MethodStatementChangeParametersRefactorChange;
import org.qmul.csar.code.java.refactor.change.MethodStatementIdentifierRefactorChange;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.query.RefactorDescriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RefactorChangeFactory {

    private final RefactorDescriptor refactorDescriptor;
    private final List<SerializableCode> objects;
    private List<RefactorChange> changes;

    public RefactorChangeFactory(RefactorDescriptor refactorDescriptor,
            List<SerializableCode> objects) {
        this.refactorDescriptor = refactorDescriptor;
        this.objects = objects;
    }

    /**
     * Creates a list representing the refactor changes corresponding to the search objects.
     */
    public RefactorChangeFactory create() {
        changes = objects.stream()
                .map(object -> changeFromRefactorTarget(refactorDescriptor, object))
                .collect(Collectors.toList());
        return this;
    }

    /**
     * Creates a map, which groups the refactor changes by file.
     */
    public Map<Path, List<RefactorChange>> groupByFile() {
        return changes.stream()
                .collect(Collectors.groupingBy(RefactorChange::path, Collectors.mapping(o -> o, Collectors.toList())));
    }

    public List<RefactorChange> getChanges() {
        return changes;
    }

    /**
     * Returns the search object representing the argument search result.
     */
    private static RefactorChange changeFromRefactorTarget(RefactorDescriptor refactorDescriptor,
            SerializableCode target) {
        if (target instanceof MethodCallExpression) {
            MethodCallExpression e = (MethodCallExpression) target;

            if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                return new MethodCallExpressionIdentifierRefactorChange(e);
            } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                return new MethodCallExpressionChangeParametersRefactorChange(e);
            }
        } else if (target instanceof MethodStatement) {
            MethodStatement m = (MethodStatement) target;

            if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
                return new MethodStatementIdentifierRefactorChange(m);
            } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
                return new MethodStatementChangeParametersRefactorChange(m);
            }
        }
        throw new RuntimeException("invalid refactor target type: " + target.getClass());
    }
}
