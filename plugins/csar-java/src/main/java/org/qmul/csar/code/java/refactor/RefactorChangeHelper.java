package org.qmul.csar.code.java.refactor;

import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.query.RefactorDescriptor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RefactorChangeHelper {

    /**
     * Creates a list representing the refactor changes corresponding to the search objects.
     */
    public static List<RefactorChange> create(RefactorDescriptor refactorDescriptor, List<SerializableCode> objects,
            TypeHierarchyResolver thr) {
        List<RefactorChange> changes = new ArrayList<>();

        objects.stream()
                .map(target -> changesFromRefactorTarget(refactorDescriptor, target, thr))
                .forEach(changes::addAll);
        return changes;
    }

    /**
     * Creates a map, which groups the refactor changes by file.
     */
    public static Map<Path, List<RefactorChange>> groupByFile(List<RefactorChange> changes) {
        return changes.stream()
                .collect(Collectors.groupingBy(RefactorChange::getPath, Collectors.mapping(o -> o, Collectors.toList())));
    }

    /**
     * Returns the refactor changes for the argument.
     */
    private static List<RefactorChange> changesFromRefactorTarget(RefactorDescriptor refactorDescriptor,
            SerializableCode target, TypeHierarchyResolver thr) {
        if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
            RefactorChangeFactory<Renamable, String> factory = createRefactorChangeFactory(refactorDescriptor, thr);
            RefactorDescriptor.Rename r = (RefactorDescriptor.Rename) refactorDescriptor;
            return factory.changes((Renamable)target, r.getIdentifierName());
        } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
            RefactorChangeFactory<ChangeableParameters, List<ParameterVariableDescriptor>> factory
                    = createRefactorChangeFactory(refactorDescriptor, thr);
            RefactorDescriptor.ChangeParameters r = (RefactorDescriptor.ChangeParameters) refactorDescriptor;
            return factory.changes((ChangeableParameters)target, r.getDescriptors());
        }
        throw new RuntimeException("invalid refactor type: " + refactorDescriptor.getClass());
    }

    /**
     * Returns a suitable {@link RefactorChangeFactory} instance for the refactor type.
     */
    public static RefactorChangeFactory createRefactorChangeFactory(RefactorDescriptor refactor,
            TypeHierarchyResolver thr) {
        if (refactor instanceof RefactorDescriptor.Rename) {
            return new RenameRefactorChangeFactory();
        } else if (refactor instanceof RefactorDescriptor.ChangeParameters) {
            return new ChangeParametersRefactorChangeFactory(thr);
        }
        throw new RuntimeException("invalid refactor type: " + refactor.getClass());
    }
}

