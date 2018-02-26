package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeHelper {

    // TODO handle whitespace throughout, etc.

    public static String normalizeVarArgs(String type) {
        if (type.endsWith("...")) {
            return type.substring(0, type.length() - 3) + "[]";
        }
        return type;
    }

    public static String removeDimensions(String type) {
        return type.replace("[]", "").replace("...", "");
    }

    public static int compareDimensions(String type1, String type2) {
        return dimensions(type1) - dimensions(type2);
    }

    public static int dimensions(String type1) {
        String t1 = removeGenericArgument(normalizeVarArgs(type1));
        return StringUtils.count(t1, "[]");
    }

    public static boolean dimensionsEquals(String type1, String type2) {
        return compareDimensions(type1, type2) == 0;
    }

    public static String removeGenericArgument(String type) {
        return type.replaceAll("<(.*)>", "");
    }

    public static String resolveGenericTypes(String type, List<String> typeParameters) {
        // Erase bounds on generics
        type = eraseBounds(type);

        // Apply type parameters
        for (String typeParameter : typeParameters) {
            type = applyTypeParameter(type, typeParameter);
        }
        return type;
    }

    public static String identifierOfGenericTypeParameter(String type) {
        int spaceIdx = type.indexOf(" ");
        return (spaceIdx > 0) ? type.substring(0, spaceIdx) : type;
    }

    private static String applyTypeParameter(String type, String typeParameter) {
        String typeParameterIdentifier = identifierOfGenericTypeParameter(typeParameter); // the type parameter
        String erasedTypeParameter = eraseBoundsOnTypeParameter(typeParameter); // what to replace it by

        Pattern p = Pattern.compile("^" + typeParameterIdentifier + "(\\[])*(\\.\\.\\.)?$");
        Matcher m = p.matcher(type);

        if (m.matches()) { // is type
            return type.replace(typeParameterIdentifier, erasedTypeParameter);
        }
        // TODO handle arrays below

        type = type.replaceAll("^" + typeParameterIdentifier + "<", erasedTypeParameter + "<"); // start with type
        type = type.replaceAll("<" + typeParameterIdentifier + "<", "<" + erasedTypeParameter + "<"); // in type param
        type = type.replaceAll("<" + typeParameterIdentifier + ">", "<" + erasedTypeParameter + ">"); // is only param
        type = type.replaceAll("<" + typeParameterIdentifier + ",", "<" + erasedTypeParameter + ","); // beginning of list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + ",", ", " + erasedTypeParameter + ","); // in list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + "<", ", " + erasedTypeParameter + "<"); // in list
        type = type.replaceAll(",\\s*" + typeParameterIdentifier + ">", ", " + erasedTypeParameter + ">"); // end of list
        return type;
    }

    public static String eraseBoundsOnTypeParameter(String typeParameter) {
        // Try to find its bounds and erase them
        Pattern p = Pattern.compile("(.*) (extends|super) (.*)(,|$)");
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

    private static String eraseBounds(String type) {
        Pattern p = Pattern.compile("\\? (extends|super) (.*)[,>]");
        Matcher m = p.matcher(type);

        while (m.find()) {
            type = type.substring(0, m.start())
                    + m.group(2)
                    + type.substring(m.end() - 1);
            m = p.matcher(type);
        }
        return type;
    }

    public static String extractGenericArgument(String type) {
        Pattern p = Pattern.compile("<(.*)>");
        Matcher m = p.matcher(type);
        return m.find() ? m.group(0) : "";
    }

    public static boolean isInbuiltType(String type) {
        switch (type) {
            case "int":
            case "float":
            case "double":
            case "long":
            case "short":
            case "byte":
            case "String":
            case "char":
            case "boolean":
            case "Object":
                return true;
            default:
                return false;
        }
    }
}
