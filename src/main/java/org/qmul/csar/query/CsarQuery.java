package org.qmul.csar.query;

import org.qmul.csar.query.domain.ContainsQuery;
import org.qmul.csar.query.domain.LanguageElement;
import org.qmul.csar.query.domain.RefactorElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CsarQuery {

    private final LanguageElement searchTarget;
    private final ContainsQuery containsQuery;
    private List<String> fromTarget;
    private final RefactorElement refactorElement;

    public CsarQuery(LanguageElement searchTarget, ContainsQuery containsQuery, List<String> fromTarget,
                     RefactorElement refactorElement) {
        this.searchTarget = searchTarget;
        this.fromTarget = fromTarget;
        this.containsQuery = containsQuery;
        this.refactorElement = refactorElement;
    }

    public CsarQuery(LanguageElement searchTarget) {
        this(searchTarget, null, null, null);
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

    public void addFromTarget(String s) {
        if (fromTarget == null)
            fromTarget = new ArrayList<>();
        fromTarget.add(s);
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
        return "CsarQuery{" +
                "searchTarget=" + searchTarget +
                ", containsQuery=" + containsQuery +
                ", fromTarget=" + fromTarget +
                ", refactorElement=" + refactorElement +
                '}';
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
}
