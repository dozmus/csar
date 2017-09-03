package org.qmul.csar.query.domain;

import java.util.Objects;

public class ContainsQueryElement {

    public static class LogicalOperatorContainsQueryElement extends ContainsQueryElement {

        private LogicalOperator logicalOperator;

        public LogicalOperatorContainsQueryElement(LogicalOperator logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        public LogicalOperator getLogicalOperator() {
            return logicalOperator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LogicalOperatorContainsQueryElement that = (LogicalOperatorContainsQueryElement) o;
            return logicalOperator == that.logicalOperator;
        }

        @Override
        public String toString() {
            return String.format("LogicalOperatorDomainElement{logicalOperator=%s}", logicalOperator);
        }
    }

    public static class LanguageElementContainsQueryElement extends ContainsQueryElement {

        private LanguageElement element;

        public LanguageElementContainsQueryElement(LanguageElement element) {
            this.element = element;
        }

        public LanguageElement getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LanguageElementContainsQueryElement that = (LanguageElementContainsQueryElement) o;
            return Objects.equals(element, that.element);
        }

        @Override
        public String toString() {
            return String.format("LanguageElementDomainElement{element=%s}", element);
        }
    }
}
