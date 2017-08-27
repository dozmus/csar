package org.qmul.csar.query;

import org.qmul.csar.query.domain.LanguageElement;

public class CsarQuery {

    private LanguageElement target;

    public CsarQuery() {
    }

    public CsarQuery(LanguageElement target) {
        this.target = target;
    }

    public LanguageElement getTarget() {
        return target;
    }

    public void setTarget(LanguageElement target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsarQuery csarQuery = (CsarQuery) o;
        return target != null ? target.equals(csarQuery.target) : csarQuery.target == null;
    }

    public enum Type {
        DEFINITION, USAGE
    }
}
