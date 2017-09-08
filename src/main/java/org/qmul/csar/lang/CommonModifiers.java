package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class CommonModifiers {

    private final CsarQuery.Type searchType;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> staticModifier;
    private final Optional<Boolean> finalModifier;
    private final Optional<Boolean> abstractModifier;
    private final Optional<Boolean> strictfpModifier;

    public CommonModifiers(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
                           Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                           Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier) {
        this.searchType = searchType;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
    }

    public CsarQuery.Type getSearchType() {
        return searchType;
    }

    public Optional<VisibilityModifier> getVisibilityModifier() {
        return visibilityModifier;
    }

    public Optional<Boolean> getStaticModifier() {
        return staticModifier;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    public Optional<Boolean> getAbstractModifier() {
        return abstractModifier;
    }

    public Optional<Boolean> getStrictfpModifier() {
        return strictfpModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonModifiers that = (CommonModifiers) o;
        return searchType == that.searchType &&
                Objects.equals(visibilityModifier, that.visibilityModifier) &&
                Objects.equals(staticModifier, that.staticModifier) &&
                Objects.equals(finalModifier, that.finalModifier) &&
                Objects.equals(abstractModifier, that.abstractModifier) &&
                Objects.equals(strictfpModifier, that.strictfpModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchType, visibilityModifier, staticModifier, finalModifier, abstractModifier,
                strictfpModifier);
    }

    @Override
    public String toString() {
        return String.format("CommonModifiers{searchType=%s, visibilityModifier=%s, staticModifier=%s, "
                + "finalModifier=%s, abstractModifier=%s, strictfpModifier=%s}", searchType, visibilityModifier,
                staticModifier, finalModifier, abstractModifier, strictfpModifier);
    }
}
