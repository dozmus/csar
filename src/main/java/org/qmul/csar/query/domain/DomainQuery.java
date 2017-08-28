package org.qmul.csar.query.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainQuery {

    private final List<DomainElement> elements = new ArrayList<>();

    public List<DomainElement> getElements() {
        return elements;
    }

    public void add(DomainElement element) {
        elements.add(element);
    }

    public void addLogicalOperator(LogicalOperator operator) {
        add(new DomainElement.LogicalOperatorDomainElement(operator));
    }

    public void addLanguageElement(LanguageElement element) {
        add(new DomainElement.LanguageElementDomainElement(element));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainQuery that = (DomainQuery) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public String toString() {
        return "DomainQuery{" +
                "elements=" + elements +
                '}';
    }
}
