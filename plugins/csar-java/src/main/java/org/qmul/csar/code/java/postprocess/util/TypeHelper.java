package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TypeHelper {

    public static String normalizeVarArgs(String type) {
        if (type.endsWith("...")) {
            return type.substring(0, type.length() - 3) + "[]";
        }
        return type;
    }

    /**
     * Returns the argument without its dimensions (array or varargs indicators).
     */
    public static String removeDimensions(String type) {
        return type.replace("[]", "").replace("...", "");
    }

    /**
     * Returns greater than 0 if the dimensions of type1 is greater than type 2's, 0 if their dimensions are the same,
     * and less than 0 if the dimensions of type 1 is less than type 2's.
     * @see #dimensions(String)
     */
    public static int compareDimensions(String type1, String type2) {
        return dimensions(type1) - dimensions(type2);
    }

    /**
     * Returns the dimensions of the argument type, this is 0 if it is not an array.
     */
    public static int dimensions(String type) {
        String t1 = removeGenericArgument(normalizeVarArgs(type));
        return StringUtils.count(t1, "[]");
    }

    /**
     * Returns <tt>true</tt> if the dimensions of type1 and type2 are equal.
     * @see #compareDimensions(String, String)
     */
    public static boolean dimensionsEquals(String type1, String type2) {
        return compareDimensions(type1, type2) == 0;
    }

    /**
     * Returns the type without its generic arguments.
     */
    public static String removeGenericArgument(String type) {
        return type.replaceAll("<(.*)>", "");
    }

    /**
     * Returns the type with its generic types resolved.
     * e.g. If type is "Class<E, V>" and typeParameters are "E extends String" and "V super E" then it returns
     * "Class<String, String>".
     * @param type
     * @param typeParameters
     * @return
     */
    public static String resolveGenericTypes(String type, List<String> typeParameters) {
        // Erase bounds on generics
        type = eraseBounds(type);

        // Apply type parameters, it's a loop in case latter type parameters resolve to prior ones
        boolean changed = true;

        for (int i = 0; i < typeParameters.size() && changed; i++) {
            changed = false;

            for (String typeParameter : typeParameters) {
                String newType = applyTypeParameter(type, typeParameter);

                while (!newType.equals(type)) {
                    type = newType;
                    changed = true;
                }
            }
        }
        return type;
    }

    /**
     * Returns the left-most part of a generic type parameter.
     * e.g. "T extends ..." returns "T".
     */
    public static String identifierOfGenericTypeParameter(String type) {
        int spaceIdx = type.indexOf(" ");
        return (spaceIdx > 0) ? type.substring(0, spaceIdx) : type;
    }

    /**
     * Returns the type with the argument type parameter applied.
     * e.g. If type is "Class<E>" and typeParameter is "E extends String" then it will return "Class<String>".
     */
    public static String applyTypeParameter(String type, String typeParameter) {
        String typeParameterIdentifier = identifierOfGenericTypeParameter(typeParameter); // the type parameter
        String erasedTypeParameter = eraseBoundsOnTypeParameter(typeParameter); // what to replace it by

        Pattern p = Pattern.compile("^" + typeParameterIdentifier + "\\s*(\\[])*\\s*(\\.\\.\\.)?$");
        Matcher m = p.matcher(type);

        if (m.matches()) { // if it is just the type
            return type.replace(typeParameterIdentifier, erasedTypeParameter);
        }

        type = type.replaceAll("^" + typeParameterIdentifier + "<", erasedTypeParameter + "<"); // start with type
        type = type.replaceAll("<" + typeParameterIdentifier + "<", "<" + erasedTypeParameter + "<"); // in type param
        type = type.replaceAll("<" + typeParameterIdentifier + ">", "<" + erasedTypeParameter + ">"); // is only param
        type = type.replaceAll("<" + typeParameterIdentifier + ",", "<" + erasedTypeParameter + ","); // beginning of list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + ",", ", " + erasedTypeParameter + ","); // in list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + "<", ", " + erasedTypeParameter + "<"); // in list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + ">", ", " + erasedTypeParameter + ">"); // end of list

        // Same as above but for arrays
        type = type.replaceAll("<" + typeParameterIdentifier + "\\[", "<" + erasedTypeParameter + "[");
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + "\\[", ", " + erasedTypeParameter + "[");
        return type;
    }

    /**
     * Returns the erased bounds on the argument type parameter.
     * e.g. "? super String" returns "String".
     */
    public static String eraseBoundsOnTypeParameter(String typeParameter) {
        // Try to find its bounds and erase them
        Pattern p = Pattern.compile("(.*)\\s+(extends|super)\\s+(.*)(,|$)");
        Matcher m = p.matcher(typeParameter);
        int matches = 0;

        while (m.find()) {
            typeParameter = m.group(3);
            m = p.matcher(typeParameter);
            matches++;
        }

        // If it has no bounds then it's an Object, otherwise it should be correct now
        return (matches == 0) ? "Object" : typeParameter;
    }

    /**
     * Erases the generic bounds of the argument type.
     * e.g. "Class<? super Integer>" returns "Class<Integer>".
     *
     */
    public static String eraseBounds(String type) {
        Pattern p = Pattern.compile("\\?\\s+(extends|super)\\s+(.*)[,>]");
        Matcher m = p.matcher(type);

        while (m.find()) {
            type = type.substring(0, m.start())
                    + m.group(2)
                    + type.substring(m.end() - 1);
            m = p.matcher(type);
        }
        return type;
    }

    /**
     * Returns the generic arguments of the argument type, including the left and right angular brackets.
     * This is the empty string if there is no generic argument.
     * e.g. "Class<E, V[]>[]" returns "<E, V[]>".
     */
    public static String extractGenericArgument(String type) {
        Pattern p = Pattern.compile("<(.*)>");
        Matcher m = p.matcher(type);
        return m.find() ? m.group(0) : "";
    }

    /**
     * Returns <tt>true</tt> if the argument type is a Java primitive or boxed primitive type, or one of:
     * <tt>String, Object</tt>.
     */
    public static boolean isInbuiltType(String type) {
        switch (type) {
            case "int":
            case "Integer":
            case "float":
            case "Float":
            case "double":
            case "Double":
            case "long":
            case "Long":
            case "short":
            case "Short":
            case "byte":
            case "Byte":
            case "String":
            case "char":
            case "Character":
            case "boolean":
            case "Boolean":
            case "Object":
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the argument dimensions as dimensions-many pairs of square brackets.
     */
    public static String dimensionsToString(int dimensions) {
        return IntStream.range(0, dimensions)
                .mapToObj(i -> "[]")
                .collect(Collectors.joining());
    }
}
