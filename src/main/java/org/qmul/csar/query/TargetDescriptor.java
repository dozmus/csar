package org.qmul.csar.query;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public class TargetDescriptor {

    private final Optional<SearchType> searchType;
    private final Descriptor descriptor;

    public TargetDescriptor(Optional<SearchType> searchType, Descriptor descriptor) {
        this.searchType = searchType;
        this.descriptor = descriptor;
    }

    public TargetDescriptor(Descriptor descriptor) {
        this(Optional.empty(), descriptor);
    }

    public Optional<SearchType> getSearchType() {
        return searchType;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetDescriptor that = (TargetDescriptor) o;
        return Objects.equals(searchType, that.searchType) && Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchType, descriptor);
    }

    @Override
    public String toString() {
        return String.format("TargetDescriptor{searchType=%s, descriptor=%s}", searchType, descriptor);
    }
}
