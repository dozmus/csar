package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class ClassLanguageElement extends IdentifiableLanguageElement {

    private CommonModifiers commonModifiers;
    private Optional<Boolean> interfaceModifier = Optional.empty();
    private Optional<Boolean> anonymous = Optional.empty();
    private Optional<Boolean> inner = Optional.empty();
    private List<String> typeParameters = new ArrayList<>();
    private List<String> superClasses = new ArrayList<>();

    public ClassLanguageElement(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
                                Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                String identifierName, Optional<Boolean> interfaceModifier,
                                Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier,
                                Optional<Boolean> anonymous, Optional<Boolean> inner, List<String> typeParameters,
                                List<String> superClasses) {
        super(LanguageElement.Type.CLASS, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier,
                abstractModifier, strictfpModifier);
        this.interfaceModifier = interfaceModifier;
        this.anonymous = anonymous;
        this.inner = inner;
        this.typeParameters = typeParameters;
        this.superClasses = superClasses;
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    public Optional<Boolean> getInterfaceModifier() {
        return interfaceModifier;
    }

    public Optional<Boolean> getAnonymous() {
        return anonymous;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassLanguageElement that = (ClassLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(interfaceModifier, that.interfaceModifier) &&
                Objects.equals(anonymous, that.anonymous) &&
                Objects.equals(inner, that.inner) &&
                Objects.equals(typeParameters, that.typeParameters) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commonModifiers, interfaceModifier, anonymous, inner, typeParameters,
                superClasses);
    }

    @Override
    public String toString() {
        return String.format("ClassLanguageElement{commonModifiers=%s, interfaceModifier=%s, anonymous=%s, inner=%s, "
                + "typeParameters=%s, superClasses=%s} %s", commonModifiers, interfaceModifier, anonymous, inner,
                typeParameters, superClasses, super.toString());
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
        private List<String> typeParameters = new ArrayList<>();
        private List<String> superClasses = new ArrayList<>();

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
                    .finalModifier(false)
                    .strictfpModifier(false)
                    .interfaceModifier(false)
                    .inner(false)
                    .anonymous(false);
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

        public Builder typeParameters(List<String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public Builder typeParameters(String... typeParameters) {
            this.typeParameters = Arrays.asList(typeParameters);
            return this;
        }

        public ClassLanguageElement build() {
            return new ClassLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier,
                    identifierName, interfaceModifier, abstractModifier, strictfpModifier, anonymous, inner,
                    typeParameters, superClasses);
        }
    }
}
