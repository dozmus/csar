package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClassLanguageElement extends LanguageElement {

    private CommonModifiers commonModifiers;
    private Optional<Boolean> interfaceModifier = Optional.empty();
    private Optional<Boolean> abstractModifier = Optional.empty();
    private Optional<Boolean> strictfpModifier = Optional.empty();
    private Optional<Boolean> anonymous = Optional.empty();
    private Optional<Boolean> inner = Optional.empty();
    private List<String> superClasses = null;

    public ClassLanguageElement() {
        setType(Type.CLASS);
        this.commonModifiers = new CommonModifiers();
    }

    public ClassLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                                Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                String identifierName, Optional<Boolean> interfaceModifier,
                                Optional<Boolean> abstractModifier, Optional<Boolean> strictfpModifier,
                                Optional<Boolean> anonymous, Optional<Boolean> inner, List<String> superClasses) {
        super(Type.CLASS, identifierName);
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
        if (superClasses == null)
            superClasses = new ArrayList<>();
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
        return Objects.equals(interfaceModifier, that.interfaceModifier) &&
                Objects.equals(abstractModifier, that.abstractModifier) &&
                Objects.equals(strictfpModifier, that.strictfpModifier) &&
                Objects.equals(anonymous, that.anonymous) &&
                Objects.equals(inner, that.inner) &&
                Objects.equals(superClasses, that.superClasses) &&
                Objects.equals(commonModifiers, that.commonModifiers);
    }
}
