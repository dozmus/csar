package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.Objects;
import java.util.Optional;

public class CommonModifiers {

    private CsarQuery.Type searchType;
    private Optional<VisibilityModifier> visibilityModifier;
    private Optional<Boolean> staticModifier = Optional.empty();
    private Optional<Boolean> finalModifier = Optional.empty();

    public CommonModifiers(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
                           Optional<Boolean> staticModifier, Optional<Boolean> finalModifier) {
        this.searchType = searchType;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonModifiers that = (CommonModifiers) o;
        return searchType == that.searchType &&
                Objects.equals(visibilityModifier, that.visibilityModifier) &&
                Objects.equals(staticModifier, that.staticModifier) &&
                Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public String toString() {
        return String.format("CommonModifiers{searchType=%s, visibilityModifier=%s, staticModifier=%s, "
                + "finalModifier=%s}", searchType, visibilityModifier, staticModifier, finalModifier);
    }
}
