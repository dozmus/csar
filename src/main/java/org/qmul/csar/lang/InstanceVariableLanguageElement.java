package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class InstanceVariableLanguageElement extends VariableLanguageElement {

    private final CommonModifiers commonModifiers;

    // TODO remove abstract and strictfp from this (i.e. CommonModifiers)

    public InstanceVariableLanguageElement(CsarQuery.Type searchType,
                                           Optional<VisibilityModifier> visibilityModifier,
                                           Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                           String identifierName, Optional<Expression> valueExpression,
                                           Optional<String> identifierType) {
        super(searchType, VariableType.INSTANCE, finalModifier, identifierName, valueExpression, identifierType);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier,
                Optional.empty(), Optional.empty());
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        org.qmul.csar.lang.InstanceVariableLanguageElement that = (org.qmul.csar.lang.InstanceVariableLanguageElement) o;
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
        private Optional<Expression> valueExpression = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public static Builder allFalse(CsarQuery.Type searchType, String identifierName) {
            return new Builder(searchType, identifierName)
                    .staticModifier(false)
                    .finalModifier(false);
        }

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

        public Builder valueExpression(Expression valueExpression) {
            this.valueExpression = Optional.of(valueExpression);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public org.qmul.csar.lang.InstanceVariableLanguageElement build() {
            return new org.qmul.csar.lang.InstanceVariableLanguageElement(searchType, visibilityModifier, staticModifier,
                    finalModifier, identifierName, valueExpression, identifierType);
        }
    }
}
