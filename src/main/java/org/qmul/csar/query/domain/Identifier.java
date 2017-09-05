package org.qmul.csar.query.domain;

import java.util.Objects;
import java.util.Optional;

public class Identifier {

    private final String type;
    private final Optional<String> name;

    public Identifier(String type, Optional<String> name) {
        this.type = type;
        this.name = name;
    }

    public Optional<String> getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return String.format("Identifier{type='%s', name=%s}", type, name);
    }
}
