package org.qmul.csar.query.domain;

import java.util.Objects;

public class DomainElement {

    public static class LogicalOperatorDomainElement extends DomainElement {

        private LogicalOperator logicalOperator;

        public LogicalOperatorDomainElement(LogicalOperator logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        public LogicalOperator getLogicalOperator() {
            return logicalOperator;
        }

        public void setLogicalOperator(LogicalOperator logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LogicalOperatorDomainElement that = (LogicalOperatorDomainElement) o;
            return logicalOperator == that.logicalOperator;
        }

        @Override
        public String toString() {
            return "LogicalOperatorDomainElement{" +
                    "logicalOperator=" + logicalOperator +
                    '}';
        }
    }

    public static class LanguageElementDomainElement extends DomainElement {

        private LanguageElement element;

        public LanguageElementDomainElement(LanguageElement element) {
            this.element = element;
        }

        public LanguageElement getElement() {
            return element;
        }

        public void setElement(LanguageElement element) {
            this.element = element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LanguageElementDomainElement that = (LanguageElementDomainElement) o;
            return Objects.equals(element, that.element);
        }

        @Override
        public String toString() {
            return "LanguageElementDomainElement{" +
                    "element=" + element +
                    '}';
        }
    }
}
