package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class MethodLanguageElement extends IdentifiableLanguageElement {

    private CommonModifiers commonModifiers;
    private String returnType;
    private Optional<Boolean> overridden = Optional.empty();
    private Optional<Integer> parameterCount = Optional.empty();
    private List<Identifier> parameters = new ArrayList<>();
    private List<String> thrownExceptions = new ArrayList<>();
    private List<String> superClasses = new ArrayList<>();

    public MethodLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                                 Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                 String identifierName, String returnType, Optional<Boolean> overridden,
                                 Optional<Integer> parameterCount, List<Identifier> parameters,
                                 List<String> thrownExceptions, List<String> superClasses) {
        super(Type.METHOD, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier);
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

    public List<Identifier> getParameters() {
        return parameters;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public String getReturnType() {
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
    public String toString() {
        return "MethodLanguageElement{" +
                "commonModifiers=" + commonModifiers +
                ", returnType='" + returnType + '\'' +
                ", overridden=" + overridden +
                ", parameterCount=" + parameterCount +
                ", parameters=" + parameters +
                ", thrownExceptions=" + thrownExceptions +
                ", superClasses=" + superClasses +
                "} " + super.toString();
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private String identifierName;
        private String returnType;
        private VisibilityModifier visibilityModifier;
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> overridden = Optional.empty();
        private Optional<Integer> parameterCount = Optional.empty();
        private List<Identifier> parameters = new ArrayList<>();
        private List<String> thrownExceptions = new ArrayList<>();
        private List<String> superClasses = new ArrayList<>();

        public Builder(CsarQuery.Type searchType, String identifierName) {
            this.searchType = searchType;
            this.identifierName = identifierName;
        }

        public Builder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = visibilityModifier;
            return this;
        }

        public Builder staticModifier(Optional<Boolean> staticModifier) {
            this.staticModifier = staticModifier;
            return this;
        }

        public Builder finalModifier(Optional<Boolean> finalModifier) {
            this.finalModifier = finalModifier;
            return this;
        }

        public Builder overridden(Optional<Boolean> overridden) {
            this.overridden = overridden;
            return this;
        }

        public Builder parameterCount(Optional<Integer> parameterCount) {
            this.parameterCount = parameterCount;
            return this;
        }

        public Builder parameters(List<Identifier> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder parameters(Identifier... parameters) {
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
                    identifierName, returnType, overridden, parameterCount, parameters, thrownExceptions, superClasses);
        }
    }
}
