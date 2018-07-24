package org.qmul.csar.lang;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class IdentifierName {

    /**
     * Returns the result of running the regular expression on the static argument if a {@link Static} is compared
     * against a {@link Regex}.
     * Returns <tt>false</tt> if a {@link Regex} is compared against a {@link Regex}.
     *
     * @param identifierName the identifier name to compare against
     * @return if the identifier names are effectively equal.
     */
    public abstract boolean nameEquals(IdentifierName identifierName);

    /**
     * An identifier name.
     */
    public static class Static extends IdentifierName {

        private String identifierName;

        public Static() {
        }

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

    /**
     * An identifier name expressed as a regular expression. This is because it was declared in a descriptor for
     * searching purposes.
     */
    public static class Regex extends IdentifierName {

        private Pattern pattern;

        public Regex() {
        }

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
