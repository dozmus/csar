package org.qmul.csar.query;

import org.qmul.csar.query.domain.ContainsQuery;
import org.qmul.csar.query.domain.LanguageElement;
import org.qmul.csar.query.domain.RefactorElement;

import java.util.*;

public final class CsarQuery {

    private final LanguageElement searchTarget;
    private final Optional<ContainsQuery> containsQuery;
    private final List<String> fromTarget;
    private final Optional<RefactorElement> refactorElement;

    public CsarQuery(LanguageElement searchTarget, Optional<ContainsQuery> containsQuery, List<String> fromTarget,
                     Optional<RefactorElement> refactorElement) {
        this.searchTarget = searchTarget;
        this.fromTarget = Collections.unmodifiableList(fromTarget);
        this.containsQuery = containsQuery;
        this.refactorElement = refactorElement;
    }

    public CsarQuery(LanguageElement searchTarget) {
        this(searchTarget, Optional.empty(), new ArrayList<>(), Optional.empty());
    }

    public LanguageElement getSearchTarget() {
        return searchTarget;
    }

    public List<String> getFromTarget() {
        return fromTarget;
    }

    public Optional<ContainsQuery> getContainsQuery() {
        return containsQuery;
    }

    public Optional<RefactorElement> getRefactorElement() {
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

    public static final class Builder {

        private final LanguageElement searchTarget;
        private Optional<ContainsQuery> containsQuery = Optional.empty();
        private List<String> fromTarget = new ArrayList<>();
        private Optional<RefactorElement> refactorElement = Optional.empty();

        public Builder(LanguageElement searchTarget) {
            this.searchTarget = searchTarget;
        }

        public Builder contains(ContainsQuery containsQuery) {
            this.containsQuery = Optional.of(containsQuery);
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
            this.refactorElement = Optional.of(refactorElement);
            return this;
        }

        public CsarQuery build() {
            return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorElement);
        }
    }
}
