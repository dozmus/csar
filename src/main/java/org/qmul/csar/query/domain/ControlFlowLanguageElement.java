package org.qmul.csar.query.domain;

import java.util.Objects;
import java.util.Optional;

public class ControlFlowLanguageElement extends LanguageElement {

    private final ControlFlowType controlFlowType;

    public ControlFlowLanguageElement(ControlFlowType controlFlowType) {
        super(Type.CONTROL_FLOW);
        this.controlFlowType = controlFlowType;
    }

    public ControlFlowType getControlFlowType() {
        return controlFlowType;
    }

    public enum ControlFlowType {
        IF, SWITCH, WHILE, DO_WHILE, FOR, FOREACH, TERNARY, SYNCHRONIZED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ControlFlowLanguageElement that = (ControlFlowLanguageElement) o;
        return controlFlowType == that.controlFlowType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), controlFlowType);
    }

    @Override
    public String toString() {
        return String.format("ControlFlowLanguageElement{controlFlowType=%s} %s", controlFlowType, super.toString());
    }

    public static class Builder {

        private ControlFlowType type;
        private Optional<String> identifierName = Optional.empty();
        private Optional<String> expr = Optional.empty();

        public Builder(ControlFlowType controlFlowType) {
            this.type = controlFlowType;
        }

        public Builder identifierName(String identifierName) {
            this.identifierName = Optional.of(identifierName);
            return this;
        }

        public Builder expr(String expr) {
            this.expr = Optional.of(expr);
            return this;
        }

        public ControlFlowLanguageElement build() {
            if (type == ControlFlowType.FOR || type == ControlFlowType.TERNARY) {
                return new ControlFlowLanguageElement(type);
            } else if (type == ControlFlowType.IF || type == ControlFlowType.WHILE
                    || type == ControlFlowType.DO_WHILE) {
                return new ExprControlFlowLanguageElement(type, expr);
            } else if (type == ControlFlowType.FOREACH) {
                return new NamedControlFlowLanguageElement(type, identifierName);
            } else {
                return new NamedExprControlFlowLanguageElement(type, identifierName, expr);
            }
        }
    }

    public static class ExprControlFlowLanguageElement extends ControlFlowLanguageElement {

        private final Optional<String> expr;

        public ExprControlFlowLanguageElement(ControlFlowType controlFlowType, Optional<String> expr) {
            super(controlFlowType);
            this.expr = expr;

            if (controlFlowType != ControlFlowType.IF && controlFlowType != ControlFlowType.WHILE
                    && controlFlowType != ControlFlowType.DO_WHILE)
                throw new RuntimeException("invalid control flow type for expr-bound control flow language element");
        }

        public Optional<String> getExpr() {
            return expr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            ExprControlFlowLanguageElement that = (ExprControlFlowLanguageElement) o;
            return Objects.equals(expr, that.expr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), expr);
        }

        @Override
        public String toString() {
            return String.format("ExprControlFlowLanguageElement{expr=%s} %s", expr, super.toString());
        }
    }

    public static class NamedControlFlowLanguageElement extends ControlFlowLanguageElement {

        private final Optional<String> identifierName;

        public NamedControlFlowLanguageElement(ControlFlowType controlFlowType, Optional<String> identifierName) {
            super(controlFlowType);
            this.identifierName = identifierName;

            if (controlFlowType != ControlFlowType.FOREACH)
                throw new RuntimeException("invalid control flow type for identifier-bound control flow language element");
        }

        public Optional<String> getExpr() {
            return identifierName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            NamedControlFlowLanguageElement that = (NamedControlFlowLanguageElement) o;
            return Objects.equals(identifierName, that.identifierName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), identifierName);
        }

        @Override
        public String toString() {
            return String.format("NamedControlFlowLanguageElement{identifierName=%s} %s", identifierName,
                    super.toString());
        }
    }

    public static class NamedExprControlFlowLanguageElement extends ControlFlowLanguageElement {

        private final Optional<String> identifierName;
        private final Optional<String> expr;

        public NamedExprControlFlowLanguageElement(ControlFlowType controlFlowType, Optional<String> identifierName,
                                                   Optional<String> expr) {
            super(controlFlowType);
            this.identifierName = identifierName;
            this.expr = expr;

            if (controlFlowType != ControlFlowType.SYNCHRONIZED && controlFlowType != ControlFlowType.SWITCH)
                throw new RuntimeException("invalid control flow type for name,expr-bound control flow language element");
        }

        public Optional<String> getIdentifierName() {
            return identifierName;
        }

        public Optional<String> getExpr() {
            return expr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            NamedExprControlFlowLanguageElement that = (NamedExprControlFlowLanguageElement) o;
            return Objects.equals(identifierName, that.identifierName) &&
                    Objects.equals(expr, that.expr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), identifierName, expr);
        }

        @Override
        public String toString() {
            return String.format("NamedExprControlFlowLanguageElement{identifierName=%s, expr=%s} %s", identifierName,
                    expr, super.toString());
        }
    }
}
