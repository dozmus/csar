package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class VariableLanguageElement extends IdentifiableLanguageElement {

    private final CsarQuery.Type searchType;
    private final VariableType variableType;
    private final Optional<String> identifierType;
    private final Optional<String> valueExpression;
    private final Optional<Boolean> finalModifier;

    public VariableLanguageElement(CsarQuery.Type searchType, VariableType variableType,
                                   Optional<Boolean> finalModifier, String identifierName,
                                   Optional<String> valueExpression, Optional<String> identifierType) {
        super(Type.VARIABLE, identifierName);
        this.searchType = searchType;
        this.variableType = variableType;
        this.identifierType = identifierType;
        this.valueExpression = valueExpression;
        this.finalModifier = finalModifier;
    }

    public CsarQuery.Type getSearchType() {
        return searchType;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<String> getValueExpression() {
        return valueExpression;
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
        return searchType == that.searchType &&
                variableType == that.variableType &&
                Objects.equals(identifierType, that.identifierType) &&
                Objects.equals(valueExpression, that.valueExpression) &&
                Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), searchType, variableType, identifierType, valueExpression, finalModifier);
    }

    @Override
    public String toString() {
        return String.format("VariableLanguageElement{searchType=%s, variableType=%s, identifierType=%s, "
                + "valueExpression=%s, finalModifier=%s} %s", searchType, variableType, identifierType, valueExpression,
                finalModifier, super.toString());
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private VariableType variableType;
        private String identifierName;
        private Optional<String> identifierType = Optional.empty();
        private Optional<String> valueExpression = Optional.empty();
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

        public Builder valueExpression(String valueExpression) {
            this.valueExpression = Optional.of(valueExpression);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public VariableLanguageElement build() {
            return new VariableLanguageElement(searchType, variableType, finalModifier, identifierName, valueExpression,
                    identifierType);
        }
    }

}
