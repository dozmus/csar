package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;

public class RefactorElement {

    public static class RenameRefactorElement extends RefactorElement {

        private final String identifierName;

        public RenameRefactorElement(String identifierName) {
            this.identifierName = identifierName;
        }

        public String getIdentifierName() {
            return identifierName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RenameRefactorElement that = (RenameRefactorElement) o;
            return Objects.equals(identifierName, that.identifierName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifierName);
        }

        @Override
        public String toString() {
            return String.format("RenameRefactorElement{identifierName='%s'} %s", identifierName, super.toString());
        }
    }

    public static class ChangeParametersRefactorElement extends RefactorElement {

        private final List<Parameter> parameters;

        public ChangeParametersRefactorElement(List<Parameter> parameters) {
            this.parameters = parameters;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChangeParametersRefactorElement that = (ChangeParametersRefactorElement) o;
            return Objects.equals(parameters, that.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parameters);
        }

        @Override
        public String toString() {
            return String.format("ChangeParametersRefactorElement{parameters=%s} %s", parameters, super.toString());
        }
    }
}
