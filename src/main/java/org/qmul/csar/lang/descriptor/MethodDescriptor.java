package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.util.OptionalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodDescriptor implements Descriptor {

    private final String identifierName;
    private final Optional<String> returnType;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> staticModifier;
    private final Optional<Boolean> finalModifier;
    private final Optional<Boolean> abstractModifier;
    private final Optional<Boolean> strictfpModifier;
    private final Optional<Boolean> synchronizedModifier;
    private final Optional<Boolean> nativeModifier;
    private final Optional<Boolean> defaultModifier;
    private final Optional<Boolean> overridden;
    private final Optional<Boolean> hasTypeArguments;
    private final Optional<Boolean> hasParameters;
    private final Optional<Boolean> hasThrownExceptions;
    private final Optional<Integer> parameterCount;
    private final List<ParameterVariableDescriptor> parameters;
    private final List<String> thrownExceptions;
    private final List<String> typeParameters;

    public MethodDescriptor(String identifierName, Optional<String> returnType,
            Optional<VisibilityModifier> visibilityModifier, Optional<Boolean> staticModifier,
            Optional<Boolean> finalModifier, Optional<Boolean> abstractModifier,
            Optional<Boolean> strictfpModifier, Optional<Boolean> synchronizedModifier,
            Optional<Boolean> nativeModifier, Optional<Boolean> defaultModifier,
            Optional<Boolean> overridden, Optional<Boolean> hasTypeArguments,
            Optional<Integer> parameterCount, List<ParameterVariableDescriptor> parameters,
            List<String> thrownExceptions, List<String> typeParameters,
            Optional<Boolean> hasParameters, Optional<Boolean> hasThrownExceptions) {
        this.identifierName = identifierName;
        this.returnType = returnType;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.synchronizedModifier = synchronizedModifier;
        this.nativeModifier = nativeModifier;
        this.defaultModifier = defaultModifier;
        this.overridden = overridden;
        this.hasTypeArguments = hasTypeArguments;
        this.parameterCount = parameterCount;
        this.parameters = parameters;
        this.thrownExceptions = thrownExceptions;
        this.typeParameters = typeParameters;
        this.hasParameters = hasParameters;
        this.hasThrownExceptions = hasThrownExceptions;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getReturnType() {
        return returnType;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Boolean> getStaticModifier() {
        return staticModifier;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    public Optional<Boolean> getAbstractModifier() {
        return abstractModifier;
    }

    public Optional<Boolean> getStrictfpModifier() {
        return strictfpModifier;
    }

    public Optional<Boolean> getSynchronizedModifier() {
        return synchronizedModifier;
    }

    public Optional<Boolean> getNativeModifier() {
        return nativeModifier;
    }

    public Optional<Boolean> getDefaultModifier() {
        return defaultModifier;
    }

    public Optional<Boolean> getOverridden() {
        return overridden;
    }

    public Optional<Boolean> getHasTypeArguments() {
        return hasTypeArguments;
    }

    public Optional<Integer> getParameterCount() {
        return parameterCount;
    }

    public List<ParameterVariableDescriptor> getParameters() {
        return parameters;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }

    public Optional<Boolean> getHasParameters() {
        return hasParameters;
    }

    public Optional<Boolean> getHasThrownExceptions() {
        return hasThrownExceptions;
    }

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDescriptor that = (MethodDescriptor) o;
        return Objects.equals(identifierName, that.identifierName) // TODO compare using regex
                && OptionalUtils.lenientEquals(returnType, that.returnType)
                && OptionalUtils.lenientEquals(visibilityModifier, that.visibilityModifier)
                && OptionalUtils.lenientEquals(staticModifier, that.staticModifier)
                && OptionalUtils.lenientEquals(finalModifier, that.finalModifier)
                && OptionalUtils.lenientEquals(abstractModifier, that.abstractModifier)
                && OptionalUtils.lenientEquals(strictfpModifier, that.strictfpModifier)
                && OptionalUtils.lenientEquals(synchronizedModifier, that.synchronizedModifier)
                && OptionalUtils.lenientEquals(nativeModifier, that.nativeModifier)
                && OptionalUtils.lenientEquals(defaultModifier, that.defaultModifier)
                && OptionalUtils.lenientEquals(overridden, that.overridden)
                && OptionalUtils.lenientEquals(hasParameters, parameters, that.hasParameters, that.parameters)
                && OptionalUtils.lenientEquals(hasThrownExceptions, thrownExceptions, that.hasThrownExceptions,
                        that.thrownExceptions)
                && OptionalUtils.lenientEquals(hasTypeArguments, typeParameters, that.hasTypeArguments,
                        that.typeParameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDescriptor that = (MethodDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(returnType, that.returnType)
                && Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(staticModifier, that.staticModifier)
                && Objects.equals(finalModifier, that.finalModifier)
                && Objects.equals(abstractModifier, that.abstractModifier)
                && Objects.equals(strictfpModifier, that.strictfpModifier)
                && Objects.equals(synchronizedModifier, that.synchronizedModifier)
                && Objects.equals(nativeModifier, that.nativeModifier)
                && Objects.equals(defaultModifier, that.defaultModifier)
                && Objects.equals(overridden, that.overridden)
                && Objects.equals(hasTypeArguments, that.hasTypeArguments)
                && Objects.equals(hasParameters, that.hasParameters)
                && Objects.equals(hasThrownExceptions, that.hasThrownExceptions)
                && Objects.equals(parameterCount, that.parameterCount)
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(thrownExceptions, that.thrownExceptions)
                && Objects.equals(typeParameters, that.typeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, returnType, visibilityModifier, staticModifier, finalModifier,
                abstractModifier, strictfpModifier, synchronizedModifier, nativeModifier, defaultModifier, overridden,
                hasTypeArguments, hasParameters, hasThrownExceptions, parameterCount, parameters, thrownExceptions,
                typeParameters);
    }

    @Override
    public String toString() {
        return String.format(
                "MethodDescriptor{identifierName='%s', returnType=%s, visibilityModifier=%s, staticModifier=%s, "
                        + "finalModifier=%s, abstractModifier=%s, strictfpModifier=%s, synchronizedModifier=%s, "
                        + "nativeModifier=%s, defaultModifier=%s, overridden=%s, hasTypeArguments=%s, "
                        + "hasParameters=%s, hasThrownExceptions=%s, parameterCount=%s, parameters=%s, "
                        + "thrownExceptions=%s, typeParameters=%s}",
                identifierName, returnType, visibilityModifier, staticModifier, finalModifier, abstractModifier,
                strictfpModifier, synchronizedModifier, nativeModifier, defaultModifier, overridden, hasTypeArguments,
                hasParameters, hasThrownExceptions, parameterCount, parameters, thrownExceptions, typeParameters);
    }

    public static class Builder {

        private String identifierName;
        private Optional<String> returnType = Optional.empty();
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> synchronizedModifier = Optional.empty();
        private Optional<Boolean> nativeModifier = Optional.empty();
        private Optional<Boolean> defaultModifier = Optional.empty();
        private Optional<Boolean> overridden = Optional.empty();
        private Optional<Boolean> hasTypeArguments = Optional.empty();
        private Optional<Boolean> hasParameters = Optional.empty();
        private Optional<Boolean> hasThrownExceptions = Optional.empty();
        private Optional<Integer> parameterCount = Optional.empty();
        private List<ParameterVariableDescriptor> parameters = new ArrayList<>();
        private List<String> thrownExceptions = new ArrayList<>();
        private List<String> typeParameters = new ArrayList<>();

        public static Builder allFalse(String identifierName) {
            return new Builder(identifierName)
                    .staticModifier(false)
                    .finalModifier(false)
                    .abstractModifier(false)
                    .strictfpModifier(false)
                    .synchronizedModifier(false)
                    .nativeModifier(false)
                    .defaultModifier(false)
                    .overridden(false)
                    .hasTypeArguments(false)
                    .hasParameters(false)
                    .hasThrownExceptions(false);
        }

        public Builder(String identifierName) {
            this.identifierName = identifierName;
        }

        public Builder returnType(String returnType) {
            this.returnType = Optional.of(returnType);
            return this;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public Builder abstractModifier(boolean abstractModifier) {
            this.abstractModifier = Optional.of(abstractModifier);
            return this;
        }

        public Builder strictfpModifier(boolean strictfpModifier) {
            this.strictfpModifier = Optional.of(strictfpModifier);
            return this;
        }

        public Builder synchronizedModifier(boolean synchronizedModifier) {
            this.synchronizedModifier = Optional.of(synchronizedModifier);
            return this;
        }

        public Builder nativeModifier(boolean nativeModifier) {
            this.nativeModifier = Optional.of(nativeModifier);
            return this;
        }

        public Builder defaultModifier(boolean defaultModifier) {
            this.defaultModifier = Optional.of(defaultModifier);
            return this;
        }

        public Builder overridden(boolean overridden) {
            this.overridden = Optional.of(overridden);
            return this;
        }

        public Builder hasTypeArguments(boolean hasTypeArguments) {
            this.hasTypeArguments = Optional.of(hasTypeArguments);
            return this;
        }

        public Builder hasThrownExceptions(boolean hasThrownExceptions) {
            this.hasThrownExceptions = Optional.of(hasThrownExceptions);
            return this;
        }

        public Builder hasParameters(boolean hasParameters) {
            this.hasParameters = Optional.of(hasParameters);
            return this;
        }

        public Builder parameterCount(int parameterCount) {
            this.parameterCount = Optional.of(parameterCount);
            return this;
        }

        public Builder parameters(List<ParameterVariableDescriptor> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder thrownExceptions(List<String> thrownExceptions) {
            this.thrownExceptions = thrownExceptions;
            return this;
        }

        public Builder typeParameters(List<String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public MethodDescriptor build() {
            return new MethodDescriptor(identifierName, returnType, visibilityModifier, staticModifier, finalModifier,
                    abstractModifier, strictfpModifier, synchronizedModifier, nativeModifier, defaultModifier,
                    overridden, hasTypeArguments, parameterCount, parameters, thrownExceptions, typeParameters,
                    hasParameters, hasThrownExceptions);
        }
    }
}
