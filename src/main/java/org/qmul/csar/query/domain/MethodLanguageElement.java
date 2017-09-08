package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class MethodLanguageElement extends IdentifiableLanguageElement {

    private final CommonModifiers commonModifiers;
    private final Optional<String> returnType;
    private final Optional<Boolean> overridden;
    private final Optional<Integer> parameterCount;
    private final List<Parameter> parameters;
    private final List<String> thrownExceptions;
    private final List<String> superClasses;

    public MethodLanguageElement(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
                                 Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                 String identifierName, Optional<String> returnType, Optional<Boolean> overridden,
                                 Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier,
                                 Optional<Integer> parameterCount, List<Parameter> parameters,
                                 List<String> thrownExceptions, List<String> superClasses) {
        super(Type.METHOD, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier,
                abstractModifier, strictfpModifier);
        this.returnType = returnType;
        this.overridden = overridden;
        this.parameterCount = parameterCount;
        this.parameters = parameters;
        this.thrownExceptions = thrownExceptions;
        this.superClasses = superClasses;
    }

    public Optional<Boolean> getOverridden() {
        return overridden;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public Optional<String> getReturnType() {
        return returnType;
    }

    public Optional<Integer> getParameterCount() {
        return parameterCount;
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodLanguageElement that = (MethodLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(overridden, that.overridden) &&
                Objects.equals(parameterCount, that.parameterCount) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(thrownExceptions, that.thrownExceptions) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commonModifiers, returnType, overridden, parameterCount, parameters,
                thrownExceptions, superClasses);
    }

    @Override
    public String toString() {
        return String.format("MethodLanguageElement{commonModifiers=%s, returnType=%s, overridden=%s, "
                + "parameterCount=%s, parameters=%s, thrownExceptions=%s, superClasses=%s} %s", commonModifiers,
                returnType, overridden, parameterCount, parameters, thrownExceptions, superClasses, super.toString());
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private String identifierName;
        private Optional<String> returnType = Optional.empty();
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> overridden = Optional.empty();
        private Optional<Integer> parameterCount = Optional.empty();
        private List<Parameter> parameters = new ArrayList<>();
        private List<String> thrownExceptions = new ArrayList<>();
        private List<String> superClasses = new ArrayList<>();

        public Builder(CsarQuery.Type searchType, String identifierName) {
            this.searchType = searchType;
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

        public Builder overridden(boolean overridden) {
            this.overridden = Optional.of(overridden);
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

        public Builder parameterCount(int parameterCount) {
            this.parameterCount = Optional.of(parameterCount);
            return this;
        }

        public Builder parameters(List<Parameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder parameters(Parameter... parameters) {
            this.parameters = Arrays.asList(parameters);
            return this;
        }

        public Builder thrownExceptions(List<String> thrownExceptions) {
            this.thrownExceptions = thrownExceptions;
            return this;
        }

        public Builder thrownExceptions(String... thrownExceptions) {
            this.thrownExceptions = Arrays.asList(thrownExceptions);
            return this;
        }

        public Builder superClasses(List<String> superClasses) {
            this.superClasses = superClasses;
            return this;
        }

        public Builder superClasses(String... superClasses) {
            this.superClasses = Arrays.asList(superClasses);
            return this;
        }

        public MethodLanguageElement build() {
            return new MethodLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier,
                    identifierName, returnType, overridden, abstractModifier, strictfpModifier, parameterCount,
                    parameters, thrownExceptions, superClasses);
        }
    }
}
