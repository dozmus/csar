package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class ClassLanguageElement extends IdentifiableLanguageElement {

    private CommonModifiers commonModifiers;
    private Optional<Boolean> interfaceModifier = Optional.empty();
    private Optional<Boolean> abstractModifier = Optional.empty();
    private Optional<Boolean> strictfpModifier = Optional.empty();
    private Optional<Boolean> anonymous = Optional.empty();
    private Optional<Boolean> inner = Optional.empty();
    private List<String> superClasses = new ArrayList<>();

    public ClassLanguageElement(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
                                Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                String identifierName, Optional<Boolean> interfaceModifier,
                                Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier,
                                Optional<Boolean> anonymous, Optional<Boolean> inner, List<String> superClasses) {
        super(LanguageElement.Type.CLASS, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier);
        this.interfaceModifier = interfaceModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.anonymous = anonymous;
        this.inner = inner;
        this.superClasses = superClasses;
    }

    public Optional<Boolean> getInterfaceModifier() {
        return interfaceModifier;
    }

    public Optional<Boolean> getAbstractModifier() {
        return abstractModifier;
    }

    public Optional<Boolean> getStrictfpModifier() {
        return strictfpModifier;
    }

    public Optional<Boolean> getAnonymous() {
        return anonymous;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassLanguageElement that = (ClassLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(interfaceModifier, that.interfaceModifier) &&
                Objects.equals(abstractModifier, that.abstractModifier) &&
                Objects.equals(strictfpModifier, that.strictfpModifier) &&
                Objects.equals(anonymous, that.anonymous) &&
                Objects.equals(inner, that.inner) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commonModifiers, interfaceModifier, abstractModifier, strictfpModifier, anonymous, inner, superClasses);
    }

    @Override
    public String toString() {
        return String.format("ClassLanguageElement{commonModifiers=%s, interfaceModifier=%s, abstractModifier=%s, "
                + "strictfpModifier=%s, anonymous=%s, inner=%s, superClasses=%s} %s", commonModifiers,
                interfaceModifier, abstractModifier, strictfpModifier, anonymous, inner, superClasses,
                super.toString());
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private String identifierName;
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> interfaceModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> anonymous = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();
        private List<String> superClasses = new ArrayList<>();

        public Builder(CsarQuery.Type searchType, String identifierName) {
            this.searchType = searchType;
            this.identifierName = identifierName;
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public Builder interfaceModifier(boolean interfaceModifier) {
            this.interfaceModifier = Optional.of(interfaceModifier);
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

        public Builder anonymous(boolean anonymous) {
            this.anonymous = Optional.of(anonymous);
            return this;
        }

        public Builder inner(boolean inner) {
            this.inner = Optional.of(inner);
            return this;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder superClasses(List<String> superClasses) {
            this.superClasses = superClasses;
            return this;
        }

        public Builder superClasses(String... superClasses) {
            this.superClasses = Arrays.asList(superClasses);
            return this;
        }

        public ClassLanguageElement build() {
            return new ClassLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier,
                    identifierName, interfaceModifier, abstractModifier, strictfpModifier, anonymous, inner,
                    superClasses);
        }
    }
}
