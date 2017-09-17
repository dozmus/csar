package org.qmul.csar.lang;

import org.qmul.csar.code.Node;
import org.qmul.csar.query.CsarQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnnotationLanguageElement extends IdentifiableLanguageElement {

    private final CommonModifiers commonModifiers;
    private final Optional<Boolean> inner;
    private final Optional<Boolean> local;
    private final List<Node> annotations;

    public AnnotationLanguageElement(CsarQuery.Type searchType, String identifierName,
            Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> staticModifier, Optional<Boolean> abstractModifier,
            Optional<Boolean> strictfpModifier, Optional<Boolean> inner, Optional<Boolean> local,
            List<Node> annotations) {
        super(Type.ANNOTATION, identifierName);
        this.inner = inner;
        this.local = local;
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, Optional.of(false),
                abstractModifier, strictfpModifier);
        this.annotations = annotations;
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public Optional<Boolean> getLocal() {
        return local;
    }

    public List<Node> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnnotationLanguageElement that = (AnnotationLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(inner, that.inner) &&
                Objects.equals(local, that.local) &&
                Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commonModifiers, inner, local, annotations);
    }

    @Override
    public String toString() {
        return String.format("AnnotationLanguageElement{commonModifiers=%s, inner=%s, local=%s, annotations=%s} %s",
                commonModifiers, inner, local, annotations, super.toString());
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private String identifierName;
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();
        private Optional<Boolean> local = Optional.empty();
        private List<Node> annotations = new ArrayList<>();

        public Builder(CsarQuery.Type searchType, String identifierName) {
            this.searchType = searchType;
            this.identifierName = identifierName;
        }

        /**
         * An instance with the argument type, identifierName and all booleans set to false.
         * @return
         */
        public static Builder allFalse(CsarQuery.Type type, String identifierName) {
            return new Builder(type, identifierName)
                    .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                    .abstractModifier(false)
                    .staticModifier(false)
                    .strictfpModifier(false)
                    .inner(false)
                    .local(false);
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public Builder abstractModifier(boolean abstractModifier) {
            this.abstractModifier = Optional.of(abstractModifier);
            return this;
        }

        public Builder strictfpModifier(boolean strictfpModifier) {
            this.strictfpModifier = Optional.of(strictfpModifier);
            return this;
        }

        public Builder inner(boolean inner) {
            this.inner = Optional.of(inner);
            return this;
        }

        public Builder local(boolean local) {
            this.local = Optional.of(local);
            return this;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder annotation(Node node) {
            this.annotations.add(node);
            return this;
        }

        public AnnotationLanguageElement build() {
            return new AnnotationLanguageElement(searchType, identifierName, visibilityModifier, staticModifier,
                    abstractModifier, strictfpModifier, inner, local, annotations);
        }
    }
}
