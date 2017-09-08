package org.qmul.csar.lang;

import java.util.Objects;

public class LanguageElement {

    private final LanguageElement.Type type;

    public LanguageElement(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageElement that = (LanguageElement) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return String.format("LanguageElement{type=%s}", type);
    }

    public enum Type {
        CLASS, METHOD, VARIABLE, CONTROL_FLOW, COMMENT
    }
}
