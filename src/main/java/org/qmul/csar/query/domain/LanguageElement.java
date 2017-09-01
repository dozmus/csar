package org.qmul.csar.query.domain;

import java.util.Objects;

public class LanguageElement {

    private Type type;
    private String identifierName;

    public LanguageElement() {
    }

    public LanguageElement(Type type, String identifierName) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguageElement)) return false;
        LanguageElement that = (LanguageElement) o;
        return type == that.type && Objects.equals(identifierName, that.identifierName);
    }

    @Override
    public String toString() {
        return String.format("LanguageElement{type=%s, identifierName='%s'}", type, identifierName);
    }

    public enum Type {
        CLASS, METHOD
    }
}
