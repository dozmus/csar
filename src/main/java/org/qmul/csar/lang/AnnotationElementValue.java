package org.qmul.csar.lang;

import org.qmul.csar.code.Node;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnnotationElementValue implements Expression {

    private Optional<String> identifierName;
    private Optional<String> identifierType;
    private Optional<Expression> value;
    private final List<Node> annotations;

    public AnnotationElementValue(Optional<String> identifierName, Optional<String> identifierType,
            Optional<Expression> value, List<Node> annotations) {
        this.identifierName = identifierName;
        this.identifierType = identifierType;
        this.value = value;
        this.annotations = annotations;
    }

    public Optional<String> getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Expression> getValue() {
        return value;
    }

    public List<Node> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationElementValue that = (AnnotationElementValue) o;
        return Objects.equals(identifierName, that.identifierName) &&
                Objects.equals(identifierType, that.identifierType) &&
                Objects.equals(value, that.value) &&
                Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, identifierType, value, annotations);
    }

    @Override
    public String toString() {
        return String.format("AnnotationElementValue{identifierName=%s, identifierType=%s, value=%s, annotations=%s}",
                identifierName, identifierType, value, annotations);
    }
}
