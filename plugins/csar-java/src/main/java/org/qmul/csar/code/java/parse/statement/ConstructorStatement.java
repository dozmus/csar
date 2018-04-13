package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A constructor.
 */
public class ConstructorStatement implements Statement {

    private final String identifierName;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Integer> parameterCount;
    private final List<ParameterVariableStatement> parameters;
    private final List<String> thrownExceptions;
    private final List<String> typeParameters;
    private final BlockStatement block;
    private final List<Annotation> annotations;

    public ConstructorStatement(String identifierName, Optional<VisibilityModifier> visibilityModifier,
            Optional<Integer> parameterCount, List<ParameterVariableStatement> parameters,
            List<String> thrownExceptions, List<String> typeParameters, BlockStatement block,
            List<Annotation> annotations) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.parameterCount = parameterCount;
        this.parameters = Collections.unmodifiableList(parameters);
        this.thrownExceptions = Collections.unmodifiableList(thrownExceptions);
        this.typeParameters = Collections.unmodifiableList(typeParameters);
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Integer> getParameterCount() {
        return parameterCount;
    }

    public List<ParameterVariableStatement> getParameters() {
        return parameters;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstructorStatement that = (ConstructorStatement) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(parameterCount, that.parameterCount)
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(thrownExceptions, that.thrownExceptions)
                && Objects.equals(typeParameters, that.typeParameters)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, visibilityModifier, parameterCount, parameters, thrownExceptions,
                typeParameters, annotations, block);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("identifierName", identifierName)
                .append("visibilityModifier", visibilityModifier)
                .append("parameterCount", parameterCount)
                .append("parameters", parameters)
                .append("thrownExceptions", thrownExceptions)
                .append("typeParameters", typeParameters)
                .append("block", block)
                .append("annotations", annotations)
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

        if (visibilityModifier.isPresent() && visibilityModifier.get() != VisibilityModifier.PACKAGE_PRIVATE) {
            builder.append(visibilityModifier.get().toString().toLowerCase()).append(" ");
        }

        if (typeParameters.size() > 0) {
            builder.append("<").append(String.join(", ", typeParameters)).append("> ");
        }
        builder.append(identifierName);

        if (parameters.size() > 0) {
            builder.append("(")
                    .append(String.join(", ", parameters.stream().map(SerializableCode::toPseudoCode)
                            .collect(Collectors.toList())))
                    .append(")");
        } else {
            builder.append("()");
        }

        if (thrownExceptions.size() > 0) {
            builder.append(" throws ").append(String.join(", ", thrownExceptions)).append("");
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

    public static class Builder {

        private final String identifierName;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Integer> parameterCount = Optional.empty();
        private List<ParameterVariableStatement> parameters = new ArrayList<>();
        private List<String> thrownExceptions = new ArrayList<>();
        private List<String> typeParameters = new ArrayList<>();
        private BlockStatement block = BlockStatement.EMPTY;
        private List<Annotation> annotations = new ArrayList<>();

        public Builder(String identifierName) {
            this.identifierName = identifierName;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder parameterCount(int parameterCount) {
            this.parameterCount = Optional.of(parameterCount);
            return this;
        }

        public Builder parameters(List<ParameterVariableStatement> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder thrownExceptions(List<String> thrownExceptions) {
            this.thrownExceptions = thrownExceptions;
            return this;
        }

        public Builder block(BlockStatement block) {
            this.block = block;
            return this;
        }

        public Builder typeParameters(List<String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public Builder annotations(List<Annotation> annotations) {
            this.annotations = annotations;
            return this;
        }

        public ConstructorStatement build() {
            return new ConstructorStatement(identifierName, visibilityModifier, parameterCount, parameters,
                    thrownExceptions, typeParameters, block, annotations);
        }
    }
}
