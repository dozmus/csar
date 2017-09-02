package org.qmul.csar.query.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RefactorElement {

    public static class RenameRefactorElement extends RefactorElement {

        private String identifierName;

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
        public String toString() {
            return String.format("RenameRefactorElement{identifierName='%s'} %s", identifierName, super.toString());
        }
    }

    public static class ChangeParameters extends RefactorElement {

        private List<Identifier> parameters = null;

        public ChangeParameters() {
        }

        public ChangeParameters(List<Identifier> parameters) {
            this.parameters = parameters;
        }

        public List<Identifier> getParameters() {
            return parameters;
        }

        public void addParameter(Identifier identifier) {
            if (parameters == null)
                parameters = new ArrayList<>();
            parameters.add(identifier);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChangeParameters that = (ChangeParameters) o;
            return Objects.equals(parameters, that.parameters);
        }

        @Override
        public String toString() {
            return String.format("ChangeParameters{identifiers=%s} %s", parameters, super.toString());
        }
    }
}
