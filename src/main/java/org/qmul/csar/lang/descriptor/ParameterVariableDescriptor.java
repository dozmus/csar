package org.qmul.csar.lang.descriptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterVariableDescriptor implements Descriptor {

    private final Optional<IdentifierName> identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public ParameterVariableDescriptor(Optional<IdentifierName> identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType; // NOTE can end with '...' (varargs) or '<...>' (generic typing)
        this.finalModifier = finalModifier;
    }

    public ParameterVariableDescriptor(IdentifierName identifierName, String identifierType, boolean finalModifier) {
        this(Optional.of(identifierName), Optional.of(identifierType), Optional.of(finalModifier));
    }

    /**
     * Returns <tt>true</tt> if the two lists have the same signature. This handles checking generic types and varargs
     * arguments as well.
     *
     * @param list1 parameters from a potential super class
     * @param list2 parameters from a potential child class
     * @return <tt>true</tt> if the two lists have the same signature
     */
    public static boolean parametersSignatureEquals(List<ParameterVariableDescriptor> list1,
            List<ParameterVariableDescriptor> list2) { // list1 is potential superclass, list2 is potential child
        if (list1.size() != list2.size())
            return false;
        final Pattern genericTypePattern = Pattern.compile("<(.*)>");

        // TODO check if types are subtypes
        for (int i = 0; i < list1.size(); i++) {
            ParameterVariableDescriptor param1 = list1.get(i);
            ParameterVariableDescriptor param2 = list2.get(i);

            if (!param1.getIdentifierType().isPresent() || !param2.getIdentifierType().isPresent())
                return false;
            String type1 = param1.getIdentifierType().get();
            String type2 = param2.getIdentifierType().get();
            type1 = normalizeVarArgs(type1);
            type2 = normalizeVarArgs(type2);

            String erasedType1 = type1.replaceAll("<(.*)>", "");
            String erasedType2 = type2.replaceAll("<(.*)>", "");

            if (!erasedType1.equals(erasedType2))
                return false;

            Matcher m1 = genericTypePattern.matcher(type1);
            Matcher m2 = genericTypePattern.matcher(type2);

            String genericType1 = m1.find() ? m1.group(1) : "";
            String genericType2 = m2.find() ? m2.group(1) : "";

            if (!genericType1.isEmpty() && genericType2.isEmpty())
                continue;

            if (genericType1.equals(genericType2))
                continue;
            return false;
        }
        return true;
    }

    private static String normalizeVarArgs(String type) {
        if (type.endsWith("...")) {
            return type.substring(0, type.length() - 3) + "[]";
        }
        return type;
    }

    public Optional<IdentifierName> getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterVariableDescriptor that = (ParameterVariableDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(identifierType, that.identifierType)
                && Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, identifierType, finalModifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifierName", identifierName)
                .append("identifierType", identifierType)
                .append("finalModifier", finalModifier)
                .toString();
    }

    public static class Builder {

        private Optional<IdentifierName> identifierName = Optional.empty();
        private Optional<String> identifierType = Optional.empty();
        private Optional<Boolean> finalModifier = Optional.empty();

        public Builder identifierName(String identifierName) {
            this.identifierName = Optional.of(new IdentifierName.Static(identifierName));
            return this;
        }

        public Builder identifierName(IdentifierName identifierName) {
            this.identifierName = Optional.of(identifierName);
            return this;
        }

        public Builder identifierType(String identifierType) {
            this.identifierType = Optional.of(identifierType);
            return this;
        }

        public Builder finalModifier(boolean finalModifier) {
            this.finalModifier = Optional.of(finalModifier);
            return this;
        }

        public ParameterVariableDescriptor build() {
            return new ParameterVariableDescriptor(identifierName, identifierType, finalModifier);
        }
    }
}
