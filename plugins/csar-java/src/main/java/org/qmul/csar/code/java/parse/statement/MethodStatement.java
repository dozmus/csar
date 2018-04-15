package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.qmul.csar.util.FilePosition;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.nio.file.Path;
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
    private final int lineNumber;
    private final int identifierStartIdx;
    private final FilePosition lParenFilePosition;
    private final FilePosition rParenFilePosition;
    private final List<FilePosition> commaFilePositions;
    private final Path path;
    /**
     * Updated by {@link MethodQualifiedTypeResolver} in post-processing.
     */
    private QualifiedType returnQualifiedType;
    /**
     * Updated by {@link MethodUseResolver} in post-processing.
     */
    private final List<MethodCallExpression> methodUsages = new ArrayList<>();

    public MethodStatement(MethodDescriptor descriptor, List<ParameterVariableStatement> params, BlockStatement block,
            List<Annotation> annotations, int lineNumber, int identifierStartIdx, FilePosition lParenFilePosition,
            FilePosition rParenFilePosition, List<FilePosition> commaFilePositions, Path path) {
        this.descriptor = descriptor;
        this.params = Collections.unmodifiableList(params);
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
        this.lineNumber = lineNumber;
        this.identifierStartIdx = identifierStartIdx;
        this.lParenFilePosition = lParenFilePosition;
        this.rParenFilePosition = rParenFilePosition;
        this.commaFilePositions = commaFilePositions;
        this.path = path;
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

    public int getIdentifierStartIdx() {
        return identifierStartIdx;
    }

    public FilePosition getlParenFilePosition() {
        return lParenFilePosition;
    }

    public FilePosition getrParenFilePosition() {
        return rParenFilePosition;
    }

    public List<FilePosition> getCommaFilePositions() {
        return commaFilePositions;
    }

    public Path getPath() {
        return path;
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
                && Objects.equals(lineNumber, that.lineNumber)
                && Objects.equals(identifierStartIdx, that.identifierStartIdx)
                && Objects.equals(lParenFilePosition, that.lParenFilePosition)
                && Objects.equals(rParenFilePosition, that.rParenFilePosition)
                && Objects.equals(commaFilePositions, that.commaFilePositions)
                && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, params, block, annotations, returnQualifiedType, methodUsages,
                identifierStartIdx, lParenFilePosition, rParenFilePosition, commaFilePositions, path);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("descriptor", descriptor)
                .append("params", params)
                .append("block", block)
                .append("annotations", annotations)
                .append("returnQualifiedType", returnQualifiedType)
                .append("methodUsages", methodUsages)
                .append("lineNumber", lineNumber)
                .append("identifierStartIdx", identifierStartIdx)
                .append("lParenFilePosition", lParenFilePosition)
                .append("rParenFilePosition", rParenFilePosition)
                .append("commaFilePositions", commaFilePositions)
                .append("path", path)
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

        if (descriptor.getStub().orElse(false)) {
            builder.append(";");
        } else if (block.equals(BlockStatement.EMPTY)) {
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
