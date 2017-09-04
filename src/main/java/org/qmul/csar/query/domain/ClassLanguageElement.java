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

    public ClassLanguageElement() {
        setType(LanguageElement.Type.CLASS);
        this.commonModifiers = new CommonModifiers();
    }

    public ClassLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
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

    public void setInterfaceModifier(Optional<Boolean> interfaceModifier) {
        this.interfaceModifier = interfaceModifier;
    }

    public Optional<Boolean> getAbstractModifier() {
        return abstractModifier;
    }

    public void setAbstractModifier(Optional<Boolean> abstractModifier) {
        this.abstractModifier = abstractModifier;
    }

    public Optional<Boolean> getStrictfpModifier() {
        return strictfpModifier;
    }

    public void setStrictfpModifier(Optional<Boolean> strictfpModifier) {
        this.strictfpModifier = strictfpModifier;
    }

    public Optional<Boolean> getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Optional<Boolean> anonymous) {
        this.anonymous = anonymous;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public void setInner(Optional<Boolean> inner) {
        this.inner = inner;
    }

    public void addSuperClass(String superClass) {
        superClasses.add(superClass);
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
    public String toString() {
        return "ClassLanguageElement{" +
                "commonModifiers=" + commonModifiers +
                ", interfaceModifier=" + interfaceModifier +
                ", abstractModifier=" + abstractModifier +
                ", strictfpModifier=" + strictfpModifier +
                ", anonymous=" + anonymous +
                ", inner=" + inner +
                ", superClasses=" + superClasses +
                "} " + super.toString();
    }

    public static class Builder {

        private CsarQuery.Type searchType;
        private VisibilityModifier visibilityModifier;
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

        public Builder staticModifier(Optional<Boolean> staticModifier) {
            this.staticModifier = staticModifier;
            return this;
        }

        public Builder finalModifier(Optional<Boolean> finalModifier) {
            this.finalModifier = finalModifier;
            return this;
        }

        public Builder interfaceModifier(Optional<Boolean> interfaceModifier) {
            this.interfaceModifier = interfaceModifier;
            return this;
        }

        public Builder abstractModifier(Optional<Boolean> abstractModifier) {
            this.abstractModifier = abstractModifier;
            return this;
        }

        public Builder strictfpModifier(Optional<Boolean> strictfpModifier) {
            this.strictfpModifier = strictfpModifier;
            return this;
        }

        public Builder anonymous(Optional<Boolean> anonymous) {
            this.anonymous = anonymous;
            return this;
        }

        public Builder inner(Optional<Boolean> inner) {
            this.inner = inner;
            return this;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = visibilityModifier;
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
