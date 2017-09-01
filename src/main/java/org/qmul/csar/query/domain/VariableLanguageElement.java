package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class VariableLanguageElement extends LanguageElement {

    protected VariableType variableType;
    private String identifierName;
    private Optional<Boolean> finalModifier = Optional.empty();

    public VariableLanguageElement() {
    }

    public VariableLanguageElement(VariableType variableType, String identifierName, Optional<Boolean> finalModifier) {
        this.variableType = variableType;
        this.identifierName = identifierName;
        this.finalModifier = finalModifier;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    @Override
    public String getIdentifierName() {
        return identifierName;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableLanguageElement that = (VariableLanguageElement) o;
        return variableType == that.variableType &&
                Objects.equals(identifierName, that.identifierName) &&
                Objects.equals(finalModifier, that.finalModifier);
    }

    public enum VariableType {
        INSTANCE, LOCAL, PARAM
    }

    public class InstanceVariableLanguageElement extends VariableLanguageElement {

        private CommonModifiers commonModifiers;

        public InstanceVariableLanguageElement() {
            this.variableType = VariableType.INSTANCE;
            this.commonModifiers = new CommonModifiers();
        }

        public InstanceVariableLanguageElement(String identifierName, CsarQuery.Type searchType,
                                               VisibilityModifier visibilityModifier, Optional<Boolean> staticModifier,
                                               Optional<Boolean> finalModifier) {
            super(VariableType.INSTANCE, identifierName, finalModifier);
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
    }
}
