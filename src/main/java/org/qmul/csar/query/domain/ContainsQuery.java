package org.qmul.csar.query.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    public String toString() {
        return String.format("ContainsQuery{elements=%s}", elements);
    }

    public static class Builder {

        private final List<ContainsQueryElement> elements = new ArrayList<>();

        public Builder addLogicalOperator(LogicalOperator operator) {
            return add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(operator));
        }

        public Builder addLanguageElement(LanguageElement element) {
            return add(new ContainsQueryElement.LanguageElementContainsQueryElement(element));
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
