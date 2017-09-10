package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class ConstructorLanguageElement extends IdentifiableLanguageElement {

    private final CsarQuery.Type searchType;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Integer> parameterCount;
    private final List<Parameter> parameters;
    private final List<String> thrownExceptions;
    private final List<String> typeParameters;

    public ConstructorLanguageElement(CsarQuery.Type searchType, String identifierName,
                                      Optional<VisibilityModifier> visibilityModifier, Optional<Integer> parameterCount,
                                      List<Parameter> parameters, List<String> thrownExceptions,
                                      List<String> typeParameters) {
        super(Type.CONSTRUCTOR, identifierName);
        this.searchType = searchType;
        this.visibilityModifier = visibilityModifier;
        this.parameterCount = parameterCount;
        this.parameters = parameters;
        this.thrownExceptions = thrownExceptions;
        this.typeParameters = typeParameters;
    }

    public CsarQuery.Type getSearchType() {
        return searchType;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Integer> getParameterCount() {
        return parameterCount;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstructorLanguageElement that = (ConstructorLanguageElement) o;
        return searchType == that.searchType &&
                Objects.equals(visibilityModifier, that.visibilityModifier) &&
                Objects.equals(parameterCount, that.parameterCount) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(thrownExceptions, that.thrownExceptions) &&
                Objects.equals(typeParameters, that.typeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), searchType, visibilityModifier, parameterCount, parameters,
                thrownExceptions, typeParameters);
    }

    @Override
    public String toString() {
        return String.format("ConstructorLanguageElement{searchType=%s, visibilityModifier=%s, parameterCount=%s, "
                + "parameters=%s, thrownExceptions=%s, typeParameters=%s} %s", searchType, visibilityModifier,
                parameterCount, parameters, thrownExceptions, typeParameters, super.toString());
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private String identifierName;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Integer> parameterCount = Optional.empty();
        private List<Parameter> parameters = new ArrayList<>();
        private List<String> thrownExceptions = new ArrayList<>();
        private List<String> typeParameters = new ArrayList<>();

        public Builder(CsarQuery.Type searchType, String identifierName) {
            this.searchType = searchType;
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

        public Builder typeParameters(List<String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public Builder typeParameters(String... typeParameters) {
            this.typeParameters = Arrays.asList(typeParameters);
            return this;
        }

        public ConstructorLanguageElement build() {
            return new ConstructorLanguageElement(searchType, identifierName, visibilityModifier, parameterCount,
                    parameters, thrownExceptions, typeParameters);
        }
    }
}
