package org.qmul.csar.query;

import java.util.*;

/**
 * A description of the task {@link org.qmul.csar.Csar} should carry out.
 * This is comprised of four parts, but only the first is required:
 * <ul>
 *     <li>searchTarget - The element to select.</li>
 *     <li>containsQuery - What searchTarget should contain within it..</li>
 *     <li>fromTarget - Where searchTarget should be found.</li>
 *     <li>refactorDescriptor - The transformation to apply to searchTarget.</li>
 * </ul>
 * <p>
 * All classes which comprise CsarQuery are immutable.
 * @see CsarQueryFactory
 */
public final class CsarQuery {

    private final TargetDescriptor searchTarget;
    private final Optional<ContainsQuery> containsQuery;
    private final List<String> fromTarget;
    private final Optional<RefactorDescriptor> refactorDescriptor;

    public CsarQuery(TargetDescriptor searchTarget, Optional<ContainsQuery> containsQuery, List<String> fromTarget,
                     Optional<RefactorDescriptor> refactorDescriptor) {
        this.searchTarget = searchTarget;
        this.fromTarget = Collections.unmodifiableList(fromTarget);
        this.containsQuery = containsQuery;
        this.refactorDescriptor = refactorDescriptor;
    }

    public CsarQuery(TargetDescriptor searchTarget) {
        this(searchTarget, Optional.empty(), new ArrayList<>(), Optional.empty());
    }

    public TargetDescriptor getSearchTarget() {
        return searchTarget;
    }

    public List<String> getFromTarget() {
        return fromTarget;
    }

    public Optional<ContainsQuery> getContainsQuery() {
        return containsQuery;
    }

    public Optional<RefactorDescriptor> getRefactorDescriptor() {
        return refactorDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsarQuery csarQuery = (CsarQuery) o;
        return Objects.equals(fromTarget, csarQuery.fromTarget) &&
                Objects.equals(searchTarget, csarQuery.searchTarget) &&
                Objects.equals(containsQuery, csarQuery.containsQuery) &&
                Objects.equals(refactorDescriptor, csarQuery.refactorDescriptor);
    }

    @Override
    public String toString() {
        return String.format("CsarQuery{searchTarget=%s, containsQuery=%s, fromTarget=%s, refactorDescriptor=%s}",
                searchTarget, containsQuery, fromTarget, refactorDescriptor);
    }

    public static final class Builder {

        private final TargetDescriptor searchTarget;
        private Optional<ContainsQuery> containsQuery = Optional.empty();
        private List<String> fromTarget = new ArrayList<>();
        private Optional<RefactorDescriptor> refactorDescriptor = Optional.empty();

        public Builder(TargetDescriptor searchTarget) {
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

        public Builder refactor(RefactorDescriptor refactorElement) {
            this.refactorDescriptor = Optional.of(refactorElement);
            return this;
        }

        public CsarQuery build() {
            return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorDescriptor);
        }
    }
}
