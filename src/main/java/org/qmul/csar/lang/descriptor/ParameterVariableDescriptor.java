package org.qmul.csar.lang.descriptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.parse.java.statement.ParameterVariableStatement;
import org.qmul.csar.code.postprocess.methodtypes.TypeSanitizer;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ParameterVariableDescriptor implements Descriptor {

    private final Optional<IdentifierName> identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public ParameterVariableDescriptor(Optional<IdentifierName> identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType; // NOTE can end with '...' (varargs) or '<...>' (generic typing)
        this.finalModifier = finalModifier;
    }

    public ParameterVariableDescriptor(IdentifierName identifierName, String identifierType, boolean finalModifier) {
        this(Optional.of(identifierName), Optional.of(identifierType), Optional.of(finalModifier));
    }

    /**
     * Returns <tt>true</tt> if the two lists have the same signature. This handles checking generic types, varargs
     * and subtypes in arguments as well.
     *
     * @param list1 parameters from a potential super class
     * @param typeParameters1
     * @param list2 parameters from a potential child class
     * @param typeParameters2
     * @return <tt>true</tt> if the two lists have the same signature
     */
    public static boolean parametersSignatureEquals(List<ParameterVariableStatement> list1,
            List<String> typeParameters1, List<ParameterVariableStatement> list2, List<String> typeParameters2,
            TypeHierarchyResolver typeHierarchyResolver) {
        // TODO ensure correctness: may breakdown
        // XXX list1 is potential superclass, list2 is potential child
        if (list1.size() != list2.size())
            return false;

        for (int i = 0; i < list1.size(); i++) {
            ParameterVariableDescriptor param1 = list1.get(i).getDescriptor();
            ParameterVariableDescriptor param2 = list2.get(i).getDescriptor();
            QualifiedType qtype1 = list1.get(i).getQualifiedType();
            QualifiedType qtype2 = list2.get(i).getQualifiedType();

            if (!param1.getIdentifierType().isPresent() || !param2.getIdentifierType().isPresent())
                return false;

            // Names
            String type1 = param1.getIdentifierType().get();
            String type2 = param2.getIdentifierType().get();
            type1 = TypeSanitizer.resolveGenericTypes(TypeSanitizer.normalizeVarArgs(type1), typeParameters1);
            type2 = TypeSanitizer.resolveGenericTypes(TypeSanitizer.normalizeVarArgs(type2), typeParameters1);
            boolean namesEqual = type1.equals(type2);
            boolean dimensionEquals = TypeSanitizer.dimensionsEquals(type1, type2);

            // Generic argument
            String genericArgument1 = TypeSanitizer.extractGenericArgument(type1);
            String genericArgument2 = TypeSanitizer.extractGenericArgument(type2);

            boolean genericTypesEqual = genericArgument1.equals(genericArgument2)
                    || !genericArgument1.isEmpty() && genericArgument2.isEmpty();

            // Check base types
            if (qtype1 != null && qtype2 != null) {
                if (!typeHierarchyResolver.isSubtype(qtype1.getQualifiedName(), qtype2.getQualifiedName())
                        || !genericTypesEqual || !dimensionEquals) {
                    return false;
                }
            } else if (!namesEqual || !genericTypesEqual || !dimensionEquals) { // fall-back comparison, to support java api
                return false;
            }
        }
        return true;
    }

    public Optional<IdentifierName> getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterVariableDescriptor that = (ParameterVariableDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(identifierType, that.identifierType)
                && Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, identifierType, finalModifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifierName", identifierName)
                .append("identifierType", identifierType)
                .append("finalModifier", finalModifier)
                .toString();
    }

    public static class Builder {

        private Optional<IdentifierName> identifierName = Optional.empty();
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder identifierName(String identifierName) {
            this.identifierName = Optional.of(new IdentifierName.Static(identifierName));
            return this;
        }

        public Builder identifierName(IdentifierName identifierName) {
            this.identifierName = Optional.of(identifierName);
            return this;
        }

        public Builder identifierType(String identifierType) {
            this.identifierType = Optional.of(identifierType);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public ParameterVariableDescriptor build() {
            return new ParameterVariableDescriptor(identifierName, identifierType, finalModifier);
        }
    }
}
