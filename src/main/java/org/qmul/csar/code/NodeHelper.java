package org.qmul.csar.code;

import org.qmul.csar.query.domain.*;

public final class NodeHelper {

    private static final String NEW_LINE = System.getProperty("line.separator");

    public static String toStringRecursively(Node node) {
        return toStringRecursively(node, new StringBuilder(), 0).toString().trim();
    }

    private static StringBuilder toStringRecursively(Node node, StringBuilder builder, int indentation) {
        builder.append(toString(node.getData())).append(NEW_LINE);

        for (int i = 0; i < node.getNodes().size(); i++) {
            Node child = node.getNodes().get(i);

            for (int k = 0; k < indentation + 1; k++)
                builder.append("  ");
            toStringRecursively(child, builder, indentation + 1);
        }
        return builder;
    }

    private static String toString(LanguageElement e) {
        if (e instanceof ClassLanguageElement) {
            ClassLanguageElement clazz = ((ClassLanguageElement)e);
            CommonModifiers common = clazz.getCommonModifiers();
            StringBuilder builder = new StringBuilder()
                    .append(clazz.getCommonModifiers().getSearchType())
                    .append(":");

            if (common.getVisibilityModifier().isPresent()
                    && common.getVisibilityModifier().get() != VisibilityModifier.PACKAGE_PRIVATE) {
                builder.append(common.getVisibilityModifier().get().toString().toLowerCase()).append(" ");
            }

            if (common.getStaticModifier().isPresent() && common.getStaticModifier().get()) {
                builder.append("static ");
            }

            if (common.getFinalModifier().isPresent() && common.getFinalModifier().get()) {
                builder.append("final ");
            }

            if (common.getStrictfpModifier().isPresent() && common.getStrictfpModifier().get()) {
                builder.append("strictfp ");
            }

            if (clazz.getInterfaceModifier().isPresent() && clazz.getInterfaceModifier().get()) {
                builder.append("interface ");
            }

            if (common.getAbstractModifier().isPresent() && common.getAbstractModifier().get()) {
                builder.append("abstract ");
            }

            if (clazz.getAnonymous().isPresent() && clazz.getAnonymous().get()) {
                builder.append("(anonymous) ");
            }

            if (clazz.getInner().isPresent() && clazz.getInner().get()) {
                builder.append("(inner) ");
            }

            builder.append("class ").append(clazz.getIdentifierName());

            if (clazz.getSuperClasses().size() > 0) {
                builder.append("(");

                for (int i = 0; i < clazz.getSuperClasses().size(); i++) {
                    String superClass = clazz.getSuperClasses().get(i);
                    builder.append(superClass);

                    if (i + 1 < clazz.getSuperClasses().size())
                        builder.append(", ");
                }
                builder.append(")");
            }
            return builder.toString();
        } else if (e instanceof MethodLanguageElement) {
            MethodLanguageElement method = ((MethodLanguageElement)e);
            CommonModifiers common = method.getCommonModifiers();
            StringBuilder builder = new StringBuilder()
                    .append(method.getCommonModifiers().getSearchType())
                    .append(":");

            if (common.getVisibilityModifier().isPresent()) {
                builder.append(common.getVisibilityModifier().get().toString().toLowerCase()).append(" ");
            }

            if (common.getStaticModifier().isPresent() && common.getStaticModifier().get()) {
                builder.append("static ");
            }

            if (common.getFinalModifier().isPresent() && common.getFinalModifier().get()) {
                builder.append("final ");
            }

            if (common.getStrictfpModifier().isPresent() && common.getStrictfpModifier().get()) {
                builder.append("strictfp ");
            }

            if (common.getAbstractModifier().isPresent() && common.getAbstractModifier().get()) {
                builder.append("abstract ");
            }

            if (method.getOverridden().isPresent() && method.getOverridden().get()) {
                builder.append("(overridden) ");
            }

            if (method.getReturnType().isPresent()) {
                builder.append(method.getReturnType().get()).append(" ");
            }

            builder.append(method.getIdentifierName());

            if (method.getParameters().size() > 0) {
                builder.append("(");

                for (int i = 0; i < method.getParameters().size(); i++) {
                    Parameter param = method.getParameters().get(i);

                    if (param.getFinalModifier().isPresent() && param.getFinalModifier().get()) {
                        builder.append("final ");
                    }

                    builder.append(param.getType());

                    if (param.getName().isPresent()) {
                        builder.append(" ").append(param.getName().get());
                    }

                    if (i + 1 < method.getParameters().size())
                        builder.append(", ");
                }
                builder.append(")");
            } else {
                builder.append("()");
            }

            if (method.getThrownExceptions().size() > 0) {
                builder.append(" throws(");

                for (int i = 0; i < method.getThrownExceptions().size(); i++) {
                    String thrownException = method.getThrownExceptions().get(i);
                    builder.append(thrownException);

                    if (i + 1 < method.getThrownExceptions().size())
                        builder.append(", ");
                }
                builder.append(")");
            }

            if (method.getSuperClasses().size() > 0) {
                builder.append(" super(");

                for (int i = 0; i < method.getSuperClasses().size(); i++) {
                    String superClass = method.getSuperClasses().get(i);
                    builder.append(superClass);

                    if (i + 1 < method.getSuperClasses().size())
                        builder.append(", ");
                }
                builder.append(")");
            }
            return builder.toString();
        } else {
            return "UNHANDLED";
        }
    }
}
