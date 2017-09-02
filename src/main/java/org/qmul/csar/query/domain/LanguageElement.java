package org.qmul.csar.query.domain;

public class LanguageElement {

    private LanguageElement.Type type;

    public LanguageElement() {
    }

    public LanguageElement(Type type) {
        this.type = type;
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
        if (o == null || getClass() != o.getClass()) return false;
        LanguageElement that = (LanguageElement) o;
        return type == that.type;
    }

    @Override
    public String toString() {
        return String.format("LanguageElement{type=%s}", type);
    }

    public enum Type {
        CLASS, METHOD, VARIABLE, CONTROL_FLOW, COMMENT
    }
}
