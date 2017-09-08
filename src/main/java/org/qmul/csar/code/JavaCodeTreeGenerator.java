package org.qmul.csar.code;

import grammars.java8pt.JavaParser;
import grammars.java8pt.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.qmul.csar.query.CsarQuery.Type.DEF;

public class JavaCodeTreeGenerator extends JavaParserBaseListener {

    // TODO class/method LE may need to be extended to be given more information - could just be bad parser code tho

    private Node root;

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

            // Modifiers
            applyClassModifiers(builder, ctx.classOrInterfaceModifier());

            // Implemented interfaces
            applyImplemented(superClasses, dec.typeList());

            // Extended class
            JavaParser.TypeTypeContext extendedClass = dec.typeType();

            if (extendedClass != null) {
                if (extendedClass.primitiveType() != null) {
                    superClasses.add(extendedClass.primitiveType().getText());
                } else if (extendedClass.classOrInterfaceType() != null) {
                    superClasses.add(extendedClass.classOrInterfaceType().getText());
                }
            }

            // Methods
            for (JavaParser.ClassBodyDeclarationContext classBody : dec.classBody().classBodyDeclaration()) {
                if (classBody.memberDeclaration() == null || classBody.memberDeclaration().methodDeclaration() == null)
                    continue;

                JavaParser.MethodDeclarationContext method = classBody.memberDeclaration().methodDeclaration();
                identifierName = method.IDENTIFIER().getText();
                MethodLanguageElement.Builder methodBuilder = new MethodLanguageElement.Builder(DEF, identifierName)
                        .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                        .staticModifier(false)
                        .finalModifier(false)
                        .overridden(false)
                        .returnType(method.typeTypeOrVoid().getText());

                // Modifiers
                for (JavaParser.ModifierContext mods : classBody.modifier()) { // TODO finish: native/synchronized?
                    if (mods.NATIVE() != null) {
                    }

                    if (mods.SYNCHRONIZED() != null) {
                    }
                    JavaParser.ClassOrInterfaceModifierContext otherMods = mods.classOrInterfaceModifier();

                    if (otherMods != null) {
                        if (otherMods.PUBLIC() != null) {
                            methodBuilder.visibilityModifier(VisibilityModifier.PUBLIC);
                        } else if (otherMods.PRIVATE() != null) {
                            methodBuilder.visibilityModifier(VisibilityModifier.PRIVATE);
                        } else if (otherMods.PROTECTED() != null) {
                            methodBuilder.visibilityModifier(VisibilityModifier.PROTECTED);
                        } else if (otherMods.ABSTRACT() != null) {
                            methodBuilder = methodBuilder.abstractModifier(true);
                        } else if (otherMods.FINAL() != null) {
                            methodBuilder.finalModifier(true);
                        } else if (otherMods.STATIC() != null) {
                            methodBuilder.staticModifier(true);
                        }
                        else if (otherMods.STRICTFP() != null) {
                            methodBuilder = methodBuilder.strictfpModifier(true);
                        }
                    }
                }

                // Parameters
                List<Parameter> paramIdentifiers = new ArrayList<>();
                List<VariableLanguageElement> params = new ArrayList<>();
                JavaParser.FormalParameterListContext paramCtx = method.formalParameters().formalParameterList();

                if (paramCtx != null) {
                    for (JavaParser.FormalParameterContext p : paramCtx.formalParameter()) {
                        String name = p.variableDeclaratorId().getText();
                        String type = typeTypeText(p.typeType());
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
                        paramIdentifiers.add(new Parameter(type, Optional.of(name)));
                    }

                    if (paramCtx.lastFormalParameter() != null) { // varargs
                        JavaParser.LastFormalParameterContext last = paramCtx.lastFormalParameter();
                        String name = last.variableDeclaratorId().getText();
                        String type = typeTypeText(last.typeType());
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
                        paramIdentifiers.add(new Parameter(type + "...", Optional.of(name)));
                    }
                }

                // Throws list
                List<String> throwsList = new ArrayList<>();

                if (method.qualifiedNameList() != null) {
                    for (JavaParser.QualifiedNameContext q : method.qualifiedNameList().qualifiedName()) {
                        for (TerminalNode identifier : q.IDENTIFIER()) {
                            throwsList.add(identifier.getText());
                        }
                    }
                }

                // TODO parse method body
                methods.add(methodBuilder
                        .parameterCount(paramIdentifiers.size())
                        .parameters(paramIdentifiers)
                        .thrownExceptions(throwsList)
                        .build());
            }
        } else if (ctx.interfaceDeclaration() != null) { // interface
            JavaParser.InterfaceDeclarationContext dec = ctx.interfaceDeclaration();
            String identifierName = dec.IDENTIFIER().getText();
            builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                    .interfaceModifier(true);

            // Modifiers
            applyClassModifiers(builder, ctx.classOrInterfaceModifier());

            // Extended interfaces
            applyImplemented(superClasses, dec.typeList());

            // Methods
            // TODO finish
        }

        // Create root
        builder.superClasses(superClasses);
        root = new Node(builder.build());

        // Append methods to root
        for (MethodLanguageElement method : methods) {
            root.addNode(new Node(method));
        }
    }

    private static String typeTypeText(JavaParser.TypeTypeContext ctx) {
        if (ctx.primitiveType() != null) {
            return ctx.primitiveType().getText();
        } else {
            return ctx.classOrInterfaceType().getText();
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

    private static void applyImplemented(List<String> superClasses, JavaParser.TypeListContext ctx) {
        if (ctx != null) {
            for (JavaParser.TypeTypeContext t : ctx.typeType()) {
                if (t.primitiveType() != null) {
                    superClasses.add(t.primitiveType().getText());
                } else if (t.classOrInterfaceType() != null) {
                    superClasses.add(t.classOrInterfaceType().getText());
                }
            }
        }
    }

    public Node getRoot() {
        return root;
    }
}
