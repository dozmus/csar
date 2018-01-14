package org.qmul.csar.lang;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class IdentifierName {

    public abstract boolean nameEquals(IdentifierName identifierName);

    public static class Static extends IdentifierName {

        private final String identifierName;

        public Static(String identifierName) {
            this.identifierName = identifierName;
        }

        public String getIdentifierName() {
            return identifierName;
        }

        @Override
        public boolean nameEquals(IdentifierName o) {
            if (this == o) return true;
            if (o == null) return false;

            if (o instanceof Static) {
                return Objects.equals(identifierName, ((Static) o).identifierName);
            } else if (o instanceof Regex) {
                return o.nameEquals(this);
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Static aStatic = (Static) o;
            return Objects.equals(identifierName, aStatic.identifierName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifierName);
        }

        @Override
        public String toString() {
            return identifierName;
        }
    }

    public static class Regex extends IdentifierName {

        private final Pattern pattern;

        public Regex(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public Regex(Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return pattern;
        }

        @Override
        public boolean nameEquals(IdentifierName o) { // NOTE comparing a regex vs a regex always returns false
            return o instanceof Static && pattern.matcher(((Static) o).identifierName).matches();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Regex regex = (Regex) o;
            return Objects.equals(pattern.pattern(), regex.pattern.pattern());
        }

        @Override
        public int hashCode() {
            return Objects.hash(pattern);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("pattern", pattern)
                    .toString();
        }
    }
}
