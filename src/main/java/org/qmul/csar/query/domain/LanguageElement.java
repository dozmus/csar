package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.Optional;

public class LanguageElement {

    private Type type;
    private CsarQuery.Type searchType;
    private VisibilityModifier visibilityModifier = null;
    private Optional<Boolean> staticModifier = Optional.empty();
    private Optional<Boolean> finalModifier = Optional.empty();
    private String identifierName;

    public LanguageElement() {
    }

    public LanguageElement(Type type, CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                           Optional<Boolean> staticModifier, Optional<Boolean> finalModifier, String identifierName) {
        this.type = type;
        this.searchType = searchType;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
        this.identifierName = identifierName;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public VisibilityModifier getVisibilityModifier() {
        return visibilityModifier;
    }

    public void setVisibilityModifier(VisibilityModifier visibilityModifier) {
        this.visibilityModifier = visibilityModifier;
    }

    public Optional<Boolean> isStaticModifier() {
        return staticModifier;
    }

    public void setStaticModifier(Optional<Boolean> staticModifier) {
        this.staticModifier = staticModifier;
    }

    public Optional<Boolean> isFinalModifier() {
        return finalModifier;
    }

    public void setFinalModifier(Optional<Boolean> finalModifier) {
        this.finalModifier = finalModifier;
    }

    public CsarQuery.Type getSearchType() {
        return searchType;
    }

    public void setSearchType(CsarQuery.Type searchType) {
        this.searchType = searchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanguageElement that = (LanguageElement) o;

        if (!staticModifier.equals(that.staticModifier)) return false;
        if (!finalModifier.equals(that.finalModifier)) return false;
        if (type != that.type) return false;
        if (searchType != that.searchType) return false;
        if (visibilityModifier != that.visibilityModifier) return false;
        return identifierName != null ? identifierName.equals(that.identifierName) : that.identifierName == null;
    }

    public enum Type {
        CLASS, METHOD
    }
}
