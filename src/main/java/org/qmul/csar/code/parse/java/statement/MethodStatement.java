package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.parse.java.expression.MethodCallExpression;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.lang.descriptor.ParameterVariableDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;
import org.qmul.csar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A method type declaration.
 */
public class MethodStatement implements Statement {

    private final MethodDescriptor descriptor;
    private final List<ParameterVariableStatement> params;
    private final BlockStatement block;
    private final List<Annotation> annotations;
    /**
     * Updated by {@link org.qmul.csar.code.postprocess.methodtypes.MethodQualifiedTypeResolver} in post-processing.
     */
    private QualifiedType returnQualifiedType;
    /**
     * Updated by {@link org.qmul.csar.code.postprocess.methodusage.MethodUsageResolver} in post-processing.
     */
    private final List<MethodCallExpression> methodUsages = new ArrayList<>();
    private final int lineNumber;

    public MethodStatement(MethodDescriptor descriptor, List<ParameterVariableStatement> params, BlockStatement block,
            List<Annotation> annotations, int lineNumber) {
        this.descriptor = descriptor;
        this.params = Collections.unmodifiableList(params);
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
        this.lineNumber = lineNumber;
    }

    public MethodDescriptor getDescriptor() {
        return descriptor;
    }

    public List<ParameterVariableStatement> getParameters() {
        return params;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public QualifiedType getReturnQualifiedType() {
        return returnQualifiedType;
    }

    public void setReturnQualifiedType(QualifiedType returnQualifiedType) {
        this.returnQualifiedType = returnQualifiedType;
    }

    public List<MethodCallExpression> getMethodUsages() {
        return methodUsages;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns if this method's signature equals the argument one, the current method is treated as one from a
     * potential superclass. So the argument descriptor is accepted if its return type or parameter types are
     * subtypes of the super's.
     * @param oMethod
     * @return
     */
    public boolean signatureEquals(MethodStatement oMethod, TypeHierarchyResolver typeHierarchyResolver) {
        // TODO fix and test generics in return types
        System.out.println(getDescriptor().signature() + " vs " + oMethod.getDescriptor().signature());
        MethodDescriptor oDescriptor = oMethod.getDescriptor();
        boolean returnTypeEquals;

        if (returnQualifiedType != null && oMethod.getReturnQualifiedType() != null) {
            returnTypeEquals = typeHierarchyResolver.isSubtype(returnQualifiedType.getQualifiedName(),
                    oMethod.getReturnQualifiedType().getQualifiedName());
        } else { // assume they can be from java api, so we dont check for correctness
            returnTypeEquals = descriptor.getReturnType().get().equals(oDescriptor.getReturnType().get());
        }
        return descriptor.getIdentifierName().equals(oDescriptor.getIdentifierName())
                && returnTypeEquals
                && ParameterVariableDescriptor.parametersSignatureEquals(descriptor.getParameters(),
                oDescriptor.getParameters());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodStatement that = (MethodStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(params, that.params)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations)
                && Objects.equals(returnQualifiedType, that.returnQualifiedType)
                && Objects.equals(methodUsages, that.methodUsages)
                && Objects.equals(lineNumber, that.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, params, block, annotations, methodUsages);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("descriptor", descriptor)
                .append("params", params)
                .append("block", block)
                .append("annotations", annotations)
                .append("returnQualifiedType", returnQualifiedType)
                .append("methodUsages", methodUsages)
                .append("lineNumber", lineNumber)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(System.lineSeparator()));
        }
        builder.append(StringUtils.indentation(indentation));

        if (descriptor.getVisibilityModifier().isPresent()
                && descriptor.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(descriptor.getVisibilityModifier().get().toPseudoCode()).append(" ");
        }
        StringUtils.append(builder, descriptor.getStaticModifier(), "static ");
        StringUtils.append(builder, descriptor.getFinalModifier(), "final ");
        StringUtils.append(builder, descriptor.getAbstractModifier(), "abstract ");
        StringUtils.append(builder, descriptor.getStrictfpModifier(), "strictfp ");
        StringUtils.append(builder, descriptor.getSynchronizedModifier(), "synchronized ");
        StringUtils.append(builder, descriptor.getNativeModifier(), "native ");
        StringUtils.append(builder, descriptor.getDefaultModifier(), "default ");
        StringUtils.append(builder, descriptor.getOverridden(), "(overridden) ");

        if (descriptor.getTypeParameters().size() > 0) {
            builder.append("<").append(String.join(", ", descriptor.getTypeParameters())).append("> ");
        }
        builder.append(descriptor.getReturnType().map(r -> r + " ").orElse(""))
                .append(descriptor.getIdentifierName());

        if (params.size() > 0) {
            builder.append("(")
                    .append(String.join(", ", params.stream()
                            .map(SerializableCode::toPseudoCode).collect(Collectors.toList())))
                    .append(")");
        } else {
            builder.append("()");
        }

        if (descriptor.getThrownExceptions().size() > 0) {
            builder.append(" throws ").append(String.join(", ", descriptor.getThrownExceptions())).append("");
        }

        if (block.equals(BlockStatement.EMPTY)) {
            builder.append(" { }");
        } else {
            builder.append(" {")
                    .append(System.lineSeparator())
                    .append(block.toPseudoCode(indentation + 1))
                    .append(System.lineSeparator())
                    .append(StringUtils.indentation(indentation))
                    .append("}");
        }
        return builder.toString();
    }
}
