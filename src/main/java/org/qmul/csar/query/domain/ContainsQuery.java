package org.qmul.csar.query.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContainsQuery {

    private final List<ContainsQueryElement> elements = new ArrayList<>();

    public List<ContainsQueryElement> getElements() {
        return elements;
    }

    public void add(ContainsQueryElement element) {
        elements.add(element);
    }

    public void addLogicalOperator(LogicalOperator operator) {
        add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(operator));
    }

    public void addLanguageElement(LanguageElement element) {
        add(new ContainsQueryElement.LanguageElementContainsQueryElement(element));
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
        return "DomainQuery{" +
                "elements=" + elements +
                '}';
    }
}
