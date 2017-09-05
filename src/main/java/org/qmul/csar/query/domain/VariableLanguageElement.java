package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class VariableLanguageElement extends IdentifiableLanguageElement {

    private final CsarQuery.Type searchType;
    private final VariableType variableType;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public VariableLanguageElement(CsarQuery.Type searchType, VariableType variableType,
                                   Optional<Boolean> finalModifier, String identifierName,
                                   Optional<String> identifierType) {
        super(Type.VARIABLE, identifierName);
        this.searchType = searchType;
        this.identifierType = identifierType;
        this.variableType = variableType;
        this.finalModifier = finalModifier;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    public CsarQuery.Type getSearchType() {
        return searchType;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableLanguageElement that = (VariableLanguageElement) o;
        return searchType == that.searchType &&
                variableType == that.variableType &&
                Objects.equals(identifierType, that.identifierType) &&
                Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), searchType, variableType, identifierType, finalModifier);
    }

    @Override
    public String toString() {
        return String.format("VariableLanguageElement{searchType=%s, variableType=%s, identifierType=%s, "
                + "finalModifier=%s} %s", searchType, variableType, identifierType, finalModifier, super.toString());
    }

    public enum VariableType {
        INSTANCE, LOCAL, PARAM
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private VariableType variableType;
        private String identifierName;
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder(CsarQuery.Type searchType, VariableType variableType, String identifierName) {
            this.searchType = searchType;
            this.variableType = variableType;
            this.identifierName = identifierName;

            if (variableType == VariableType.INSTANCE)
                throw new IllegalArgumentException("variableType must not be INSTANCE");
        }

        public Builder identifierType(String identifierType) {
            this.identifierType = Optional.of(identifierType);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public VariableLanguageElement build() {
            return new VariableLanguageElement(searchType, variableType, finalModifier, identifierName, identifierType);
        }
    }

    public static class InstanceVariableLanguageElement extends VariableLanguageElement {

        private CommonModifiers commonModifiers;

        public InstanceVariableLanguageElement(CsarQuery.Type searchType,
                                               Optional<VisibilityModifier> visibilityModifier,
                                               Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                               String identifierName, Optional<String> identifierType) {
            super(searchType, VariableType.INSTANCE, finalModifier, identifierName, identifierType);
            this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier);
        }

        public CommonModifiers getCommonModifiers() {
            return commonModifiers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            InstanceVariableLanguageElement that = (InstanceVariableLanguageElement) o;
            return Objects.equals(commonModifiers, that.commonModifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), commonModifiers);
        }

        @Override
        public String toString() {
            return String.format("InstanceVariableLanguageElement{commonModifiers=%s} %s", commonModifiers,
                    super.toString());
        }

        public static class Builder {

            private CsarQuery.Type searchType;
            private String identifierName;
            private Optional<String> identifierType = Optional.empty();
            private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
            private Optional<Boolean> staticModifier = Optional.empty();
            private Optional<Boolean> finalModifier = Optional.empty();

            public Builder(CsarQuery.Type searchType, String identifierName) {
                this.searchType = searchType;
                this.identifierName = identifierName;
            }

            public Builder identifierType(String identifierType) {
                this.identifierType = Optional.of(identifierType);
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

            public InstanceVariableLanguageElement build() {
                return new InstanceVariableLanguageElement(searchType, visibilityModifier, staticModifier,
                        finalModifier, identifierName, identifierType);
            }
        }
    }
}
