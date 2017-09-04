package org.qmul.csar.query.domain;

import java.util.Objects;

public class IdentifiableLanguageElement extends LanguageElement {

    private String identifierName;

    public IdentifiableLanguageElement() {
    }

    public IdentifiableLanguageElement(LanguageElement.Type type, String identifierName) {
        super(type);
        this.identifierName = identifierName;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IdentifiableLanguageElement that = (IdentifiableLanguageElement) o;
        return Objects.equals(identifierName, that.identifierName);
    }

    @Override
    public String toString() {
        return String.format("IdentifiableLanguageElement{type=%s, identifierName='%s'}", getType(), identifierName);
    }
}
