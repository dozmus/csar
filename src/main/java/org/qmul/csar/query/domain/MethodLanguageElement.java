package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodLanguageElement extends IdentifiableLanguageElement {

    private CommonModifiers commonModifiers;
    private String returnType;
    private Optional<Boolean> overridden = Optional.empty();
    private Optional<Integer> parameterCount = Optional.empty();
    private List<Identifier> parameters;
    private List<String> thrownExceptions;
    private List<String> superClasses;

    public MethodLanguageElement() {
        setType(Type.METHOD);
        this.commonModifiers = new CommonModifiers();
    }

    public MethodLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                                 Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                 String identifierName, String returnType, Optional<Boolean> overridden,
                                 Optional<Integer> parameterCount, List<Identifier> parameters,
                                 List<String> thrownExceptions, List<String> superClasses) {
        super(Type.METHOD, identifierName);
        this.commonModifiers = new CommonModifiers(searchType, visibilityModifier, staticModifier, finalModifier);
        this.returnType = returnType;
        this.overridden = overridden;
        this.parameterCount = parameterCount;
        this.parameters = parameters;
        this.thrownExceptions = thrownExceptions;
        this.superClasses = superClasses;
    }

    public Optional<Boolean> getOverridden() {
        return overridden;
    }

    public void setOverridden(Optional<Boolean> overridden) {
        this.overridden = overridden;
    }

    public void addSuperClass(String superClass) {
        if (superClasses == null)
            superClasses = new ArrayList<>();
        superClasses.add(superClass);
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public void addParameter(Identifier parameter) {
        if (parameters == null)
            parameters = new ArrayList<>();
        parameters.add(parameter);
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public void addThrownException(String thrownException) {
        if (thrownExceptions == null)
            thrownExceptions = new ArrayList<>();
        thrownExceptions.add(thrownException);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Optional<Integer> getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(Optional<Integer> parameterCount) {
        this.parameterCount = parameterCount;
    }

    public CommonModifiers getCommonModifiers() {
        return commonModifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodLanguageElement that = (MethodLanguageElement) o;
        return Objects.equals(commonModifiers, that.commonModifiers) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(overridden, that.overridden) &&
                Objects.equals(parameterCount, that.parameterCount) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(thrownExceptions, that.thrownExceptions) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public String toString() {
        return "MethodLanguageElement{" +
                "commonModifiers=" + commonModifiers +
                ", returnType='" + returnType + '\'' +
                ", overridden=" + overridden +
                ", parameterCount=" + parameterCount +
                ", parameters=" + parameters +
                ", thrownExceptions=" + thrownExceptions +
                ", superClasses=" + superClasses +
                "} " + super.toString();
    }
}
