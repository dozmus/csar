package org.qmul.csar.query;

import org.qmul.csar.lang.Descriptor;

import java.util.*;

public class ContainsQuery {

    private final List<ContainsQueryElement> elements;

    public ContainsQuery(List<ContainsQueryElement> elements) {
        this.elements = elements;
    }

    public List<ContainsQueryElement> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainsQuery that = (ContainsQuery) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return String.format("ContainsQuery{elements=%s}", elements);
    }

    public static class Builder {

        private final List<ContainsQueryElement> elements = new ArrayList<>();

        public Builder addLogicalOperator(LogicalOperator operator) {
            return add(new ContainsQueryElement.LogicalOperator(operator));
        }

        public Builder addTargetDescriptor(TargetDescriptor element) {
            return add(new ContainsQueryElement.TargetDescriptor(element));
        }

        public Builder addTargetDescriptor(SearchType type, Descriptor descriptor) {
            TargetDescriptor td = new TargetDescriptor(Optional.of(type), descriptor);
            return add(new ContainsQueryElement.TargetDescriptor(td));
        }

        public Builder add(ContainsQueryElement element) {
            elements.add(element);
            return this;
        }

        public Builder addAll(Collection<ContainsQueryElement> elements) {
            this.elements.addAll(elements);
            return this;
        }

        public ContainsQuery build() {
            return new ContainsQuery(elements);
        }
    }
}
