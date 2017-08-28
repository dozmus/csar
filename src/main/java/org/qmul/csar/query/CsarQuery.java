package org.qmul.csar.query;

import org.qmul.csar.query.domain.DomainQuery;
import org.qmul.csar.query.domain.LanguageElement;

import java.util.Objects;

public final class CsarQuery {

    private final LanguageElement target;
    private final LanguageElement fromTarget;
    private final DomainQuery domain;

    public CsarQuery(LanguageElement target, DomainQuery domain, LanguageElement fromTarget) {
        this.target = target;
        this.fromTarget = fromTarget;
        this.domain = domain;
    }

    public CsarQuery(LanguageElement target) {
        this(target, null, null);
    }

    public LanguageElement getTarget() {
        return target;
    }

    public LanguageElement getFromTarget() {
        return fromTarget;
    }

    public DomainQuery getDomain() {
        return domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsarQuery csarQuery = (CsarQuery) o;
        return Objects.equals(target, csarQuery.target) &&
                Objects.equals(fromTarget, csarQuery.fromTarget) &&
                Objects.equals(domain, csarQuery.domain);
    }

    @Override
    public String toString() {
        return "CsarQuery{" +
                "target=" + target +
                ", fromTarget=" + fromTarget +
                ", domain=" + domain +
                '}';
    }

    public enum Type {
        DEFINITION, USAGE
    }
}
