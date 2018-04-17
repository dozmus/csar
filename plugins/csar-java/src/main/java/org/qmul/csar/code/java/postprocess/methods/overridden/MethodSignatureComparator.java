package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.parse.statement.ParameterVariableStatement;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.code.java.postprocess.util.TypeHelper;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;

import java.util.List;

public final class MethodSignatureComparator {

    /**
     * Returns if the two lists have the same signature. This handles checking generic types, varargs
     * and subtypes in arguments as well.
     *
     * @param list1 parameters from a potential super class
     * @param list2 parameters from a potential child class
     * @return if the two lists have the same signature
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
            TypeInstance qtype1 = list1.get(i).getTypeInstance();
            TypeInstance qtype2 = list2.get(i).getTypeInstance();

            if (!param1.getIdentifierType().isPresent() || !param2.getIdentifierType().isPresent())
                return false;

            // Names
            String type1 = param1.getIdentifierType().get();
            String type2 = param2.getIdentifierType().get();
            type1 = TypeHelper.resolveGenericTypes(TypeHelper.normalizeVarArgs(type1), typeParameters1);
            type2 = TypeHelper.resolveGenericTypes(TypeHelper.normalizeVarArgs(type2), typeParameters2);
            boolean namesEqual = type1.equals(type2);
            boolean dimensionEquals = TypeHelper.dimensionsEquals(type1, type2);

            // Generic argument
            String genericArgument1 = TypeHelper.extractGenericArgument(type1);
            String genericArgument2 = TypeHelper.extractGenericArgument(type2);

            boolean genericTypesEqual = genericArgument1.equals(genericArgument2)
                    || !genericArgument1.isEmpty() && genericArgument2.isEmpty();

            // Check base types
            if (qtype1 != null && qtype2 != null) {
                dimensionEquals = qtype1.getDimensions() == qtype2.getDimensions();

                if (!typeHierarchyResolver.isSubtype(qtype1.getQualifiedName(), qtype2.getQualifiedName())
                        || !genericTypesEqual || !dimensionEquals) {
                    return false;
                }
            } else {
                if (!namesEqual || !genericTypesEqual || !dimensionEquals) { // fall-back comparison, to support java api)
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns if this method's signature equals the argument one, the current method is treated as one from a
     * potential superclass. So the argument descriptor is accepted if its return type or parameter types are
     * subtypes of the super's.
     */
    public static boolean signatureEquals(MethodStatement method, MethodStatement oMethod,
            TypeHierarchyResolver typeHierarchyResolver) {
        // TODO ensure correctness: may breakdown
        MethodDescriptor descriptor = method.getDescriptor();
        MethodDescriptor oDescriptor = oMethod.getDescriptor();
        boolean returnTypeEquals;
        boolean parameterTypeEquals;

        // Return type
        String type1 = descriptor.getReturnType().get();
        String type2 = oDescriptor.getReturnType().get();
        type1 = TypeHelper.resolveGenericTypes(TypeHelper.normalizeVarArgs(type1), descriptor.getTypeParameters());
        type2 = TypeHelper.resolveGenericTypes(TypeHelper.normalizeVarArgs(type2), oDescriptor.getTypeParameters());

        if (method.getReturnQualifiedType() != null && oMethod.getReturnQualifiedType() != null) {
            returnTypeEquals = typeHierarchyResolver.isSubtype(method.getReturnQualifiedType().getQualifiedName(),
                    oMethod.getReturnQualifiedType().getQualifiedName())
                    && TypeHelper.dimensionsEquals(type1, type2);
        } else { // assume they can be from java api, so we don't check for correctness
            returnTypeEquals = type1.equals(type2) && TypeHelper.dimensionsEquals(type1, type2);
        }

        if (!descriptor.getReturnType().isPresent() || !oDescriptor.getReturnType().isPresent()) {
            return false;
        }

        // Parameter type
        parameterTypeEquals = parametersSignatureEquals(method.getParameters(),
                descriptor.getTypeParameters(), oMethod.getParameters(), oDescriptor.getTypeParameters(),
                typeHierarchyResolver);
        return descriptor.getIdentifierName().equals(oDescriptor.getIdentifierName())
                && returnTypeEquals
                && parameterTypeEquals;
    }
}
