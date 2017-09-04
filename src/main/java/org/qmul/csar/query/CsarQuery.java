package org.qmul.csar.query;

import org.qmul.csar.query.domain.ContainsQuery;
import org.qmul.csar.query.domain.LanguageElement;
import org.qmul.csar.query.domain.RefactorElement;

import java.util.*;

public final class CsarQuery {

    private final LanguageElement searchTarget;
    private final ContainsQuery containsQuery;
    private List<String> fromTarget;
    private final RefactorElement refactorElement;

    public CsarQuery(LanguageElement searchTarget, ContainsQuery containsQuery, List<String> fromTarget,
                     RefactorElement refactorElement) {
        this.searchTarget = searchTarget;
        this.fromTarget = Collections.unmodifiableList(fromTarget);
        this.containsQuery = containsQuery;
        this.refactorElement = refactorElement;
    }

    public CsarQuery(LanguageElement searchTarget) {
        this(searchTarget, null, new ArrayList<>(), null);
    }

    public LanguageElement getSearchTarget() {
        return searchTarget;
    }

    public List<String> getFromTarget() {
        return fromTarget;
    }

    public ContainsQuery getContainsQuery() {
        return containsQuery;
    }

    public RefactorElement getRefactorElement() {
        return refactorElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsarQuery csarQuery = (CsarQuery) o;
        return Objects.equals(fromTarget, csarQuery.fromTarget) &&
                Objects.equals(searchTarget, csarQuery.searchTarget) &&
                Objects.equals(containsQuery, csarQuery.containsQuery) &&
                Objects.equals(refactorElement, csarQuery.refactorElement);
    }

    @Override
    public String toString() {
        return String.format("CsarQuery{searchTarget=%s, containsQuery=%s, fromTarget=%s, refactorElement=%s}",
                searchTarget, containsQuery, fromTarget, refactorElement);
    }

    public enum Type {
        /**
         * A definition of an element.
         */
        DEF,
        /**
         * A usage of a defined element.
         */
        USE
    }

    public static class Builder {

        private final LanguageElement searchTarget;
        private ContainsQuery containsQuery;
        private List<String> fromTarget = new ArrayList<>();
        private RefactorElement refactorElement;

        public Builder(LanguageElement searchTarget) {
            this.searchTarget = searchTarget;
        }

        public Builder contains(ContainsQuery containsQuery) {
            this.containsQuery = containsQuery;
            return this;
        }

        public Builder from(List<String> fromTarget) {
            this.fromTarget = fromTarget;
            return this;
        }

        public Builder from(String... fromTarget) {
            this.fromTarget = Arrays.asList(fromTarget);
            return this;
        }

        public Builder refactor(RefactorElement refactorElement) {
            this.refactorElement = refactorElement;
            return this;
        }

        public CsarQuery build() {
            return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorElement);
        }
    }
}
