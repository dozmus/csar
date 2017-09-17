package org.qmul.csar.lang;

import org.qmul.csar.code.Node;

import java.util.List;
import java.util.Objects;

public class EnumConstantLanguageElement extends IdentifiableLanguageElement {

    private final List<Expression> arguments;
    private final List<Node> body;

    public EnumConstantLanguageElement(String identifierName, List<Expression> arguments, List<Node> body) {
        super(Type.ENUM_CONSTANT, identifierName);
        this.arguments = arguments;
        this.body = body;
    }


    public List<Expression> getArguments() {
        return arguments;
    }

    public List<Node> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnumConstantLanguageElement that = (EnumConstantLanguageElement) o;
        return Objects.equals(arguments, that.arguments) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), arguments, body);
    }

    @Override
    public String toString() {
        return String.format("EnumConstantLanguageElement{arguments=%s, body=%s} %s", arguments, body,
                super.toString());
    }
}
