package org.qmul.csar.query.domain;

import java.util.Objects;

public class NamedLanguageElement extends LanguageElement {

    private String identifierName;

    public NamedLanguageElement() {
    }

    public NamedLanguageElement(LanguageElement.Type type, String identifierName) {
        super(type);
        this.identifierName = identifierName;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NamedLanguageElement that = (NamedLanguageElement) o;
        return Objects.equals(identifierName, that.identifierName);
    }

    @Override
    public String toString() {
        return String.format("NamedTypedLanguageElement{type=%s, identifierName='%s'}", getType(), identifierName);
    }
}
