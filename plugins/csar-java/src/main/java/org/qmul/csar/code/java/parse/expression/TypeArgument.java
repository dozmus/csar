package org.qmul.csar.code.java.parse.expression;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.Optional;

public interface TypeArgument extends Expression {

    class Type implements TypeArgument {

        private final String identifierType;

        public Type(String identifierType) {
            this.identifierType = identifierType;
        }

        public String getIdentifierType() {
            return identifierType;
        }

        @Override
        public String toPseudoCode(int indentation) {
            return StringUtils.indentation(indentation) + " " + identifierType;
        }
    }

    class Bounds implements TypeArgument {

        private final Optional<String> identifierType;
        private final Type type;

        public Bounds(Optional<String> identifierType, Type type) {
            this.identifierType = identifierType;
            this.type = type;
        }

        public Optional<String> getIdentifierType() {
            return identifierType;
        }

        public Type getType() {
            return type;
        }

        @Override
        public String toPseudoCode(int indentation) {
            String end = identifierType.map(i -> " " + type.toString().toLowerCase() + " " + identifierType)
                    .orElse("");
            return StringUtils.indentation(indentation) + "?" + end;
        }

        public enum Type {
            NONE, SUPER, EXTENDS
        }
    }
}
