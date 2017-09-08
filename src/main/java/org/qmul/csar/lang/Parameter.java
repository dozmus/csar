package org.qmul.csar.lang;

import java.util.Objects;
import java.util.Optional;

public class Parameter {

    private final String type;
    private final Optional<String> name;
    private final Optional<Boolean> finalModifier;

    public Parameter(String type, Optional<String> name, Optional<Boolean> finalModifier) {
        this.type = type;
        this.name = name;
        this.finalModifier = finalModifier;
    }

    public Parameter(String type, Optional<String> name) {
        this(type, name, Optional.empty());
    }

    public Optional<String> getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(type, parameter.type) &&
                Objects.equals(name, parameter.name) &&
                Objects.equals(finalModifier, parameter.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, finalModifier);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "type='" + type + '\'' +
                ", name=" + name +
                ", finalModifier=" + finalModifier +
                '}';
    }
}
