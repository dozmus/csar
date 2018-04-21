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

    /**
     * Returns a refactorer supporting the argument descriptor.
     *
     * @param writeToFiles if the changes of the refactor should be written to the files
     *
     * @throws IllegalArgumentException if the argument descriptor is not supported
     */
    public static Refactorer create(RefactorDescriptor refactorDescriptor, boolean writeToFiles) {
        if (refactorDescriptor instanceof RefactorDescriptor.Rename) {
            String newName = ((RefactorDescriptor.Rename) refactorDescriptor).getIdentifierName();
            return new RenameRefactorer(newName, writeToFiles);
        } else if (refactorDescriptor instanceof RefactorDescriptor.ChangeParameters) {
            List<ParameterVariableDescriptor> descriptors
                    = ((RefactorDescriptor.ChangeParameters)refactorDescriptor).getDescriptors();
            return new ChangeParametersRefactorer(descriptors, writeToFiles);
        }
        throw new IllegalArgumentException("unsupported refactor target: " + refactorDescriptor.getClass().getName());
    }
}
