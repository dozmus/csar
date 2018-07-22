package org.qmul.csar.query;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.descriptors.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A description of a refactor.
 */
public interface RefactorDescriptor {

    /**
     * Returns <tt>true</tt> if the argument is a valid target for this refactor.
     *
     * @param desc the descriptor to validate for
     * returns <tt>true</tt> if this descriptor is a valid target for this refactor.
     */
    boolean validate(TargetDescriptor desc);

    /**
     * Returns an array of warning messages, containing possible side-effects of this refactor.
     */
    String[] warnings();

    class Rename implements RefactorDescriptor {

        private final String identifierName;

        public Rename(String identifierName) {
            this.identifierName = identifierName;
        }

        public String getIdentifierName() {
            return identifierName;
        }

        @Override
        public boolean validate(TargetDescriptor desc) {
            Descriptor descriptor = desc.getDescriptor();
            // XXX clean up by introducing enum Type to Descriptor
            return descriptor instanceof ClassDescriptor || descriptor instanceof MethodDescriptor
                    || descriptor instanceof EnumDescriptor || descriptor instanceof AbstractVariableDescriptor
                    || descriptor instanceof AnnotationDescriptor;
        }

        @Override
        public String[] warnings() {
            return new String[] {
                    "A method with the new name may exist in contexts where refactors occurred."
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rename that = (Rename) o;
            return Objects.equals(identifierName, that.identifierName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifierName);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("identifierName", identifierName)
                    .toString();
        }
    }

    class ChangeParameters implements RefactorDescriptor {

        private final List<ParameterVariableDescriptor> descriptors;

        public ChangeParameters(List<ParameterVariableDescriptor> descriptors) {
            this.descriptors = Collections.unmodifiableList(descriptors);
        }

        public List<ParameterVariableDescriptor> getDescriptors() {
            return descriptors;
        }

        @Override
        public boolean validate(TargetDescriptor desc) {
            // Check no parameter identifier has been used twice
            Set<String> identifiers = descriptors.stream()
                    .map(d -> d.getIdentifierName().get().toString())
                    .collect(Collectors.toSet());
            return desc.getDescriptor() instanceof MethodDescriptor && identifiers.size() == descriptors.size();
        }

        @Override
        public String[] warnings() {
            return new String[] {
                    "A method with the new parameters may exist in contexts where refactors occurred."
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChangeParameters that = (ChangeParameters) o;
            return Objects.equals(descriptors, that.descriptors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(descriptors);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("descriptors", descriptors)
                    .toString();
        }
    }
}
