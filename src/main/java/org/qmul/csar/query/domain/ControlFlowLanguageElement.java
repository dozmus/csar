package org.qmul.csar.query.domain;

import java.util.Objects;

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
        IF, SWITCH, WHILE, DOWHILE, FOR, FOREACH, TERNARY, SYNCHRONIZED
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
    public String toString() {
        return String.format("ControlFlowLanguageElement{controlFlowType=%s} %s", controlFlowType, super.toString());
    }

    public static class ExprControlFlowLanguageElement extends ControlFlowLanguageElement {

        private String expr;

        public ExprControlFlowLanguageElement(ControlFlowType controlFlowType, String expr) {
            super(controlFlowType);
            this.expr = expr;

            if (controlFlowType != ControlFlowType.IF && controlFlowType != ControlFlowType.WHILE
                    && controlFlowType != ControlFlowType.DOWHILE)
                throw new RuntimeException("invalid control flow type for expr-bound control flow language element");
        }

        public String getExpr() {
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
        public String toString() {
            return String.format("ExprControlFlowLanguageElement{expr='%s'} %s", expr, super.toString());
        }
    }

    public static class NamedControlFlowLanguageElement extends ControlFlowLanguageElement {

        private String identifierName;

        public NamedControlFlowLanguageElement(ControlFlowType controlFlowType, String identifierName) {
            super(controlFlowType);
            this.identifierName = identifierName;

            if (controlFlowType != ControlFlowType.FOREACH)
                throw new RuntimeException("invalid control flow type for identifier-bound control flow language element");
        }

        public String getExpr() {
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
        public String toString() {
            return String.format("ExprControlFlowLanguageElement{identifierName='%s'} %s", identifierName, super.toString());
        }
    }

    public static class NamedExprControlFlowLanguageElement extends ControlFlowLanguageElement {

        private String identifierName;
        private String expr;

        public NamedExprControlFlowLanguageElement(ControlFlowType controlFlowType, String identifierName,
                                                   String expr) {
            super(controlFlowType);
            this.identifierName = identifierName;
            this.expr = expr;

            if (controlFlowType != ControlFlowType.SYNCHRONIZED && controlFlowType != ControlFlowType.SWITCH)
                throw new RuntimeException("invalid control flow type for name,expr-bound control flow language element");
        }

        public String getIdentifierName() {
            return identifierName;
        }

        public String getExpr() {
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
        public String toString() {
            return String.format("NamedExprControlFlowLanguageElement{identifierName='%s', expr='%s'} %s",
                    identifierName, expr, super.toString());
        }
    }
}
