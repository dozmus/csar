package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class VariableLanguageElement extends NamedLanguageElement {

    private CsarQuery.Type searchType;
    protected VariableType variableType;
    private String identifierType;
    private Optional<Boolean> finalModifier = Optional.empty();

    public VariableLanguageElement() {
        super(Type.VARIABLE, null);
    }

    public VariableLanguageElement(CsarQuery.Type searchType, VariableType variableType,
                                   Optional<Boolean> finalModifier, String identifierName, String identifierType) {
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

    public String getIdentifierType() {
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
    public String toString() {
        return "VariableLanguageElement{" +
                "searchType=" + searchType +
                ", variableType=" + variableType +
                ", identifierType='" + identifierType + '\'' +
                ", finalModifier=" + finalModifier +
                "} " + super.toString();
    }

    public enum VariableType {
        INSTANCE, LOCAL, PARAM
    }

    public static class InstanceVariableLanguageElement extends VariableLanguageElement {

        private CommonModifiers commonModifiers;

        public InstanceVariableLanguageElement() {
            this.variableType = VariableType.INSTANCE;
            this.commonModifiers = new CommonModifiers();
        }

        public InstanceVariableLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                                               Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                               String identifierName, String identifierType) {
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
        public String toString() {
            return String.format("InstanceVariableLanguageElement{commonModifiers=%s} %s", commonModifiers,
                    super.toString());
        }
    }
}
