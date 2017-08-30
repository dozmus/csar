package org.qmul.csar.query.domain;

import org.qmul.csar.query.CsarQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodLanguageElement extends LanguageElement {

    private String returnType;
    private Optional<Boolean> overridden = Optional.empty();
    private Optional<Integer> parameterCount = Optional.empty();
    private List<Identifier> parameters = null;
    private List<String> thrownExceptions = null;
    private List<String> superClasses = null;

    public MethodLanguageElement() {
        setType(Type.METHOD);
    }

    public MethodLanguageElement(CsarQuery.Type searchType, VisibilityModifier visibilityModifier,
                                 Optional<Boolean> staticModifier, Optional<Boolean> finalModifier,
                                 String identifierName, String returnType, Optional<Boolean> overridden,
                                 Optional<Integer> parameterCount, List<Identifier> parameters,
                                 List<String> thrownExceptions, List<String> superClasses) {
        super(Type.METHOD, searchType, visibilityModifier, staticModifier, finalModifier, identifierName);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodLanguageElement that = (MethodLanguageElement) o;
        return Objects.equals(returnType, that.returnType) &&
                Objects.equals(overridden, that.overridden) &&
                Objects.equals(parameterCount, that.parameterCount) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(thrownExceptions, that.thrownExceptions) &&
                Objects.equals(superClasses, that.superClasses);
    }
}
