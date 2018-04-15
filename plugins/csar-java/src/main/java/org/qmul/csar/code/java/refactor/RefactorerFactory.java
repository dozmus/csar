package org.qmul.csar.code.java.refactor;

import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.query.RefactorDescriptor;

import java.util.List;

/**
 * A refactorer factory.
 */
public final class RefactorerFactory {

    private RefactorerFactory() {
    }

    public static Refactorer create(RefactorDescriptor refactorDescriptor, boolean writeToFiles) {
        if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
            String newName = ((RefactorDescriptor.Rename) refactorDescriptor).getIdentifierName();
            return new RenameRefactorer(newName, writeToFiles);
        } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
            List<ParameterVariableDescriptor> descriptors
                    = ((RefactorDescriptor.ChangeParameters)refactorDescriptor).getDescriptors();
            return new ChangeParametersRefactorer(descriptors, writeToFiles);
        } else {
            throw new IllegalArgumentException("unsupported refactor target: "
                    + refactorDescriptor.getClass().getName());
        }
    }
}
