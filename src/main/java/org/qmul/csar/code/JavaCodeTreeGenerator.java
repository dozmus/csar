package org.qmul.csar.code;

import grammars.java8pt.JavaParser;
import grammars.java8pt.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.qmul.csar.query.CsarQuery.Type.DEF;

public final class JavaCodeTreeGenerator extends JavaParserBaseListener {

    // TODO class/method LE may need to be extended to be given more information - could just be bad parser code tho

    private Node root;

    private static void parseParameters(JavaParser.FormalParameterListContext ctx, List<Parameter> paramIdentifiers,
                                        List<VariableLanguageElement> params) {
        if (ctx == null)
            return;
        for (JavaParser.FormalParameterContext p : ctx.formalParameter()) { // regular args
            String name = p.variableDeclaratorId().getText();
            String type = p.typeType().getText();
            boolean finalModifier = false;

            for (JavaParser.VariableModifierContext vm : p.variableModifier()) {
                if (vm.FINAL() != null) {
                    finalModifier = true;
                    break;
                }
            }
            params.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type)
                    .finalModifier(finalModifier)
                    .build());
            paramIdentifiers.add(new Parameter(type, Optional.of(name), Optional.of(finalModifier)));
        }

        if (ctx.lastFormalParameter() != null) { // varargs
            JavaParser.LastFormalParameterContext last = ctx.lastFormalParameter();
            String name = last.variableDeclaratorId().getText();
            String type = last.typeType().getText();
            boolean finalModifier = false;

            for (JavaParser.VariableModifierContext vm : last.variableModifier()) {
                if (vm.FINAL() != null) {
                    finalModifier = true;
                    break;
                }
            }
            params.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type + "...")
                    .finalModifier(finalModifier)
                    .build());
            paramIdentifiers.add(new Parameter(type + "...", Optional.of(name), Optional.of(finalModifier)));
        }
    }

    private static void applyClassModifiers(ClassLanguageElement.Builder builder,
                                            List<JavaParser.ClassOrInterfaceModifierContext> ctx) {
        for (JavaParser.ClassOrInterfaceModifierContext mods : ctx) {
            if (mods.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mods.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mods.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mods.ABSTRACT() != null) {
                builder.abstractModifier(true);
            } else if (mods.FINAL() != null) {
                builder.finalModifier(true);
            } else if (mods.STATIC() != null) {
                builder.staticModifier(true);
            } else if (mods.STRICTFP() != null) {
                builder.strictfpModifier(true);
            }
        }
    }

    private static void parseImplemented(List<String> superClasses, JavaParser.TypeListContext ctx) {
        if (ctx == null)
            return;
        for (JavaParser.TypeTypeContext t : ctx.typeType()) {
            superClasses.add(t.getText());
        }
    }

    private static List<String> parseThrows(JavaParser.QualifiedNameListContext ctx) {
        List<String> throwsList = new ArrayList<>();

        for (JavaParser.QualifiedNameContext q : ctx.qualifiedName()) {
            for (TerminalNode identifier : q.IDENTIFIER()) {
                throwsList.add(identifier.getText());
            }
        }
        return throwsList;
    }

    private static void applyMethodModifiers(MethodLanguageElement.Builder methodBuilder,
                                             JavaParser.ModifierContext ctx) {
        // TODO impl native/synchronized?
        if (ctx.NATIVE() != null) {
        }

        if (ctx.SYNCHRONIZED() != null) {
        }
        JavaParser.ClassOrInterfaceModifierContext mods = ctx.classOrInterfaceModifier();

        if (mods != null) {
            if (mods.PUBLIC() != null) {
                methodBuilder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mods.PRIVATE() != null) {
                methodBuilder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mods.PROTECTED() != null) {
                methodBuilder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mods.ABSTRACT() != null) {
                methodBuilder.abstractModifier(true);
            } else if (mods.FINAL() != null) {
                methodBuilder.finalModifier(true);
            } else if (mods.STATIC() != null) {
                methodBuilder.staticModifier(true);
            }  else if (mods.STRICTFP() != null) {
                methodBuilder.strictfpModifier(true);
            }
        }
    }

    private static MethodLanguageElement.Builder parseMethodSkeleton(TerminalNode identifier,
                                                                     JavaParser.TypeTypeOrVoidContext type,
                                                                     List<JavaParser.ModifierContext> modifiers,
                                                                     JavaParser.FormalParameterListContext parameterCtx,
                                                                     JavaParser.QualifiedNameListContext throwsCtx,
                                                                     boolean overridden) {
        String identifierName = identifier.getText();
        MethodLanguageElement.Builder methodBuilder = MethodLanguageElement.Builder.allFalse(DEF, identifierName)
                .overridden(overridden)
                .returnType(type.getText());

        // Modifiers
        for (JavaParser.ModifierContext modifier : modifiers) {
            applyMethodModifiers(methodBuilder, modifier);
        }

        // Parameters
        List<Parameter> paramIdentifiers = new ArrayList<>();
        List<VariableLanguageElement> params = new ArrayList<>();
        parseParameters(parameterCtx, paramIdentifiers, params);

        // Throws list
        List<String> throwsList = (throwsCtx == null) ? new ArrayList<>() : parseThrows(throwsCtx);

        return methodBuilder
                .parameterCount(paramIdentifiers.size())
                .parameters(paramIdentifiers)
                .thrownExceptions(throwsList);
    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        // TODO impl
    }

    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        // TODO impl
    }

    @Override
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        // NOTE this method is unfinished
        // Check if node type is handled
        if (ctx.classDeclaration() == null && ctx.interfaceDeclaration() == null) {
            // TODO error: stop generating and tell the codeparser to skip the file
            System.err.println("Unhandled file type");
            return;
        }

        ClassLanguageElement.Builder builder = null;
        List<String> superClasses = new ArrayList<>();
        List<MethodLanguageElement> methods = new ArrayList<>();

        // Generate node
        if (ctx.classDeclaration() != null) { // class
            JavaParser.ClassDeclarationContext dec = ctx.classDeclaration();
            String identifierName = dec.IDENTIFIER().getText();
            builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName);

            // Class modifiers
            applyClassModifiers(builder, ctx.classOrInterfaceModifier());

            // Implemented interfaces
            parseImplemented(superClasses, dec.typeList());

            // Extended class
            JavaParser.TypeTypeContext extendedClass = dec.typeType();

            if (extendedClass != null) {
                superClasses.add(extendedClass.getText());
            }

            // Methods
            for (JavaParser.ClassBodyDeclarationContext classBody : dec.classBody().classBodyDeclaration()) {
                if (classBody.memberDeclaration() == null || classBody.memberDeclaration().methodDeclaration() == null)
                    continue;

                JavaParser.MethodDeclarationContext method = classBody.memberDeclaration().methodDeclaration();
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), classBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);
                // TODO finish (incl. parse method body)
                methods.add(methodBuilder.build());
            }
        } else if (ctx.interfaceDeclaration() != null) { // interface
            JavaParser.InterfaceDeclarationContext dec = ctx.interfaceDeclaration();
            String identifierName = dec.IDENTIFIER().getText();
            builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                    .interfaceModifier(true);

            // Class modifiers
            applyClassModifiers(builder, ctx.classOrInterfaceModifier());

            // Extended interfaces
            parseImplemented(superClasses, dec.typeList());

            // Methods
            for (JavaParser.InterfaceBodyDeclarationContext intBody : dec.interfaceBody().interfaceBodyDeclaration()) {
                JavaParser.InterfaceMethodDeclarationContext method = intBody.interfaceMemberDeclaration()
                        .interfaceMethodDeclaration();

                if (method == null || method.interfaceMethodModifier() == null)
                    continue;

                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);

                for (JavaParser.InterfaceMethodModifierContext mods : method.interfaceMethodModifier()) {
                    if (mods.PUBLIC() != null) {
                        methodBuilder.visibilityModifier(VisibilityModifier.PUBLIC);
                    } else if (mods.ABSTRACT() != null) {
                        methodBuilder.abstractModifier(true);
                    } else if (mods.STATIC() != null) {
                        methodBuilder.staticModifier(true);
                    } else if (mods.STRICTFP() != null) {
                        methodBuilder.strictfpModifier(true);
                    } else if (mods.DEFAULT() != null) {
                        // TODO set
                    }
                }

                // TODO finish
                methods.add(methodBuilder.build());
            }
        }

        // Create root
        builder.superClasses(superClasses);
        root = new Node(builder.build());

        // Append methods to root
        for (MethodLanguageElement method : methods) {
            root.addNode(new Node(method));
        }
    }

    public Node getRoot() {
        return root;
    }
}
