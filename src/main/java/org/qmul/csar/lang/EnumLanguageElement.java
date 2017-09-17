package org.qmul.csar.lang;

import org.qmul.csar.query.CsarQuery;

import java.util.*;

public class EnumLanguageElement extends IdentifiableLanguageElement {

    private CommonModifiers commonModifiers;
    private Optional<Boolean> inner = Optional.empty();
    private Optional<Boolean> local = Optional.empty();
    private List<String> superClasses = new ArrayList<>();

    public EnumLanguageElement(CsarQuery.Type searchType, Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> staticModifier, Optional<Boolean> finalModifier, String identifierName,
            Optional<Boolean> strictfpModifier, Optional<Boolean> inner, Optional<Boolean> local,
            List<String> superClasses) {
        super(LanguageElement.Type.ENUM, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier,
                Optional.of(false), strictfpModifier);
        this.inner = inner;
        this.local = local;
        this.superClasses = superClasses;
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

    public List<String> getSuperClasses() {
        return superClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnumLanguageElement that = (EnumLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(inner, that.inner) &&
                Objects.equals(local, that.local) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commonModifiers, inner, local, superClasses);
    }

    @Override
    public String toString() {
        return String.format("EnumLanguageElement{commonModifiers=%s, inner=%s, local=%s, superClasses=%s} %s",
                commonModifiers, inner, local, superClasses, super.toString());
    }
    
    public static class Builder {

        private CsarQuery.Type searchType;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private String identifierName;
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> anonymous = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();
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
                    .staticModifier(false)
                    .finalModifier(false)
                    .strictfpModifier(false)
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

        public EnumLanguageElement build() {
            return new EnumLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier,
                    identifierName, strictfpModifier, anonymous, inner, superClasses);
        }
    }
}
