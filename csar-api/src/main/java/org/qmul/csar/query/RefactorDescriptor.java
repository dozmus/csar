package org.qmul.csar.query;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.descriptors.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface RefactorDescriptor {

    /**
     * Returns <tt>true</tt> if the argument is a valid target for this refactor.
     *
     * @param desc the descriptor to validate for
     * returns <tt>true</tt> if this descriptor is a valid target for this refactor.
     */
    boolean validate(TargetDescriptor desc);

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
            return desc.getDescriptor() instanceof MethodDescriptor;
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
