package org.qmul.csar.lang;

import org.qmul.csar.code.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Parameter {

    private final String type;
    private final Optional<String> name;
    private final Optional<Boolean> finalModifier;
    private final List<Node> annotations;

    public Parameter(String type, Optional<String> name, Optional<Boolean> finalModifier,
            List<Node> annotations) {
        this.type = type;
        this.name = name;
        this.finalModifier = finalModifier;
        this.annotations = annotations;
    }

    public Parameter(String type, Optional<String> name) {
        this(type, name, Optional.empty(), new ArrayList<>());
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

    public List<Node> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(type, parameter.type) &&
                Objects.equals(name, parameter.name) &&
                Objects.equals(finalModifier, parameter.finalModifier) &&
                Objects.equals(annotations, parameter.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, finalModifier, annotations);
    }

    @Override
    public String toString() {
        return String.format("Parameter{type='%s', name=%s, finalModifier=%s, annotations=%s}", type, name,
                finalModifier, annotations);
    }
}
