package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.util.OptionalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClassDescriptor implements Descriptor {

    private final IdentifierName identifierName;
    private final Optional<VisibilityModifier> visibilityModifier;
    private final Optional<Boolean> staticModifier;
    private final Optional<Boolean> finalModifier;
    private final Optional<Boolean> abstractModifier;
    private final Optional<Boolean> strictfpModifier;
    private final Optional<Boolean> interfaceModifier;
    private final Optional<Boolean> inner;
    private final Optional<Boolean> local;
    private final Optional<Boolean> anonymous;
    private final Optional<String> extendedClass;
    private final Optional<Boolean> hasTypeArguments;
    private final Optional<Boolean> hasImplementedInterfaces;
    private final List<String> typeParameters;
    private final List<String> implementedInterfaces;

    public ClassDescriptor(IdentifierName identifierName, Optional<VisibilityModifier> visibilityModifier,
            Optional<Boolean> staticModifier, Optional<Boolean> finalModifier, Optional<Boolean> abstractModifier,
            Optional<Boolean> strictfpModifier, Optional<Boolean> interfaceModifier, Optional<Boolean> inner,
            Optional<Boolean> local, Optional<Boolean> anonymous, Optional<Boolean> hasTypeArguments,
            List<String> typeParameters, Optional<Boolean> hasImplementedInterfaces, List<String> implementedInterfaces,
            Optional<String> extendedClass) {
        this.identifierName = identifierName;
        this.visibilityModifier = visibilityModifier;
        this.staticModifier = staticModifier;
        this.finalModifier = finalModifier;
        this.abstractModifier = abstractModifier;
        this.strictfpModifier = strictfpModifier;
        this.interfaceModifier = interfaceModifier;
        this.inner = inner;
        this.local = local;
        this.anonymous = anonymous;
        this.hasTypeArguments = hasTypeArguments;
        this.typeParameters = typeParameters;
        this.hasImplementedInterfaces = hasImplementedInterfaces;
        this.implementedInterfaces = implementedInterfaces;
        this.extendedClass = extendedClass;
    }

    public IdentifierName getIdentifierName() {
        return identifierName;
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

    public Optional<Boolean> getInterfaceModifier() {
        return interfaceModifier;
    }

    public Optional<Boolean> getInner() {
        return inner;
    }

    public Optional<Boolean> getLocal() {
        return local;
    }

    public Optional<Boolean> getAnonymous() {
        return anonymous;
    }

    public Optional<String> getExtendedClass() {
        return extendedClass;
    }

    public List<String> getTypeParameters() {
        return typeParameters;
    }

    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    public Optional<Boolean> getHasTypeArguments() {
        return hasTypeArguments;
    }

    public Optional<Boolean> getHasImplementedInterfaces() {
        return hasImplementedInterfaces;
    }

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassDescriptor that = (ClassDescriptor) o;
        return identifierName.nameEquals(that.identifierName)
                && OptionalUtils.lenientEquals(visibilityModifier, that.visibilityModifier)
                && OptionalUtils.lenientEquals(staticModifier, that.staticModifier)
                && OptionalUtils.lenientEquals(finalModifier, that.finalModifier)
                && OptionalUtils.lenientEquals(abstractModifier, that.abstractModifier)
                && OptionalUtils.lenientEquals(strictfpModifier, that.strictfpModifier)
                && OptionalUtils.lenientEquals(interfaceModifier, that.interfaceModifier)
                && OptionalUtils.lenientEquals(inner, that.inner)
                && OptionalUtils.lenientEquals(local, that.local)
                && OptionalUtils.lenientEquals(anonymous, that.anonymous)
                && OptionalUtils.lenientEquals(extendedClass, that.extendedClass)
                && OptionalUtils.lenientEquals(hasImplementedInterfaces, implementedInterfaces,
                that.hasImplementedInterfaces, that.implementedInterfaces)
                && OptionalUtils.lenientEquals(hasTypeArguments, typeParameters, that.hasTypeArguments,
                that.typeParameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassDescriptor that = (ClassDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(visibilityModifier, that.visibilityModifier)
                && Objects.equals(staticModifier, that.staticModifier)
                && Objects.equals(finalModifier, that.finalModifier)
                && Objects.equals(abstractModifier, that.abstractModifier)
                && Objects.equals(strictfpModifier, that.strictfpModifier)
                && Objects.equals(interfaceModifier, that.interfaceModifier)
                && Objects.equals(inner, that.inner)
                && Objects.equals(local, that.local)
                && Objects.equals(anonymous, that.anonymous)
                && Objects.equals(extendedClass, that.extendedClass)
                && Objects.equals(hasTypeArguments, that.hasTypeArguments)
                && Objects.equals(hasImplementedInterfaces, that.hasImplementedInterfaces)
                && Objects.equals(typeParameters, that.typeParameters)
                && Objects.equals(implementedInterfaces, that.implementedInterfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, visibilityModifier, staticModifier, finalModifier, abstractModifier,
                strictfpModifier, interfaceModifier, inner, local, anonymous, extendedClass, hasTypeArguments,
                hasImplementedInterfaces, typeParameters, implementedInterfaces);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifierName", identifierName)
                .append("visibilityModifier", visibilityModifier)
                .append("staticModifier", staticModifier)
                .append("finalModifier", finalModifier)
                .append("abstractModifier", abstractModifier)
                .append("strictfpModifier", strictfpModifier)
                .append("interfaceModifier", interfaceModifier)
                .append("inner", inner)
                .append("local", local)
                .append("anonymous", anonymous)
                .append("extendedClass", extendedClass)
                .append("hasTypeArguments", hasTypeArguments)
                .append("hasImplementedInterfaces", hasImplementedInterfaces)
                .append("typeParameters", typeParameters)
                .append("implementedInterfaces", implementedInterfaces)
                .toString();
    }

    public static class Builder {

        private final IdentifierName identifierName;
        private Optional<VisibilityModifier> visibilityModifier = Optional.empty();
        private Optional<Boolean> staticModifier = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();
        private Optional<Boolean> abstractModifier = Optional.empty();
        private Optional<Boolean> strictfpModifier = Optional.empty();
        private Optional<Boolean> interfaceModifier = Optional.empty();
        private Optional<Boolean> inner = Optional.empty();
        private Optional<Boolean> local = Optional.empty();
        private Optional<Boolean> anonymous = Optional.empty();
        private Optional<Boolean> hasTypeArguments = Optional.empty();
        private Optional<Boolean> hasImplementedInterfaces = Optional.empty();
        private List<String> typeParameters = new ArrayList<>();
        private List<String> implementedInterfaces = new ArrayList<>();
        private Optional<String> extendedClass = Optional.empty();

        public static Builder allFalse(String identifierName) {
            return new Builder(identifierName)
                    .staticModifier(false)
                    .finalModifier(false)
                    .abstractModifier(false)
                    .strictfpModifier(false)
                    .interfaceModifier(false)
                    .inner(false)
                    .local(false)
                    .anonymous(false)
                    .hasImplementedInterfaces(false)
                    .hasTypeArguments(false);
        }

        public Builder(String identifierName) {
            this.identifierName = new IdentifierName.Static(identifierName);
        }

        public Builder(IdentifierName identifierName) {
            this.identifierName = identifierName;
        }

        public Builder visibilityModifier(VisibilityModifier visibilityModifier) {
            this.visibilityModifier = Optional.of(visibilityModifier);
            return this;
        }

        public Builder staticModifier(boolean staticModifier) {
            this.staticModifier = Optional.of(staticModifier);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
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

        public Builder interfaceModifier(boolean interfaceModifier) {
            this.interfaceModifier = Optional.of(interfaceModifier);
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

        public Builder anonymous(boolean anonymous) {
            this.anonymous = Optional.of(anonymous);
            return this;
        }

        public Builder typeParameters(List<String> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public Builder extendedClass(String extendedClass) {
            this.extendedClass = Optional.of(extendedClass);
            return this;
        }

        public Builder implementedInterfaces(List<String> implementedInterfaces) {
            this.implementedInterfaces = implementedInterfaces;
            return this;
        }

        public Builder hasTypeArguments(boolean hasTypeArguments) {
            this.hasTypeArguments = Optional.of(hasTypeArguments);
            return this;
        }

        public Builder hasImplementedInterfaces(boolean hasImplementedInterfaces) {
            this.hasImplementedInterfaces = Optional.of(hasImplementedInterfaces);
            return this;
        }

        public ClassDescriptor build() {
            return new ClassDescriptor(identifierName, visibilityModifier, staticModifier, finalModifier,
                    abstractModifier, strictfpModifier, interfaceModifier, inner, local, anonymous, hasTypeArguments,
                    typeParameters, hasImplementedInterfaces, implementedInterfaces, extendedClass);
        }
    }
}
