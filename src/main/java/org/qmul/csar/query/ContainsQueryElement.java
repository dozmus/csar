package org.qmul.csar.query;

import java.util.Objects;

public abstract class ContainsQueryElement {

    public static class LogicalOperatorContainsQueryElement extends ContainsQueryElement {

        private final LogicalOperator logicalOperator;

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
        public int hashCode() {
            return Objects.hash(logicalOperator);
        }

        @Override
        public String toString() {
            return String.format("LogicalOperatorDomainElement{logicalOperator=%s}", logicalOperator);
        }
    }

    public static class TargetDescriptorContainsQueryElement extends ContainsQueryElement {

        private final TargetDescriptor targetDescriptor;

        public TargetDescriptorContainsQueryElement(TargetDescriptor targetDescriptor) {
            this.targetDescriptor = targetDescriptor;
        }

        public TargetDescriptor getTargetDescriptor() {
            return targetDescriptor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TargetDescriptorContainsQueryElement that = (TargetDescriptorContainsQueryElement) o;
            return Objects.equals(targetDescriptor, that.targetDescriptor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetDescriptor);
        }

        @Override
        public String toString() {
            return String.format("LanguageElementDomainElement{targetDescriptor=%s}", targetDescriptor);
        }
    }
}
