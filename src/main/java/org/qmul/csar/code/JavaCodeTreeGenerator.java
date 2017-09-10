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

    private Node rootNode;

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

    private static void applyTypeParameters(ClassLanguageElement.Builder builder,
                                            JavaParser.TypeParametersContext ctx) {
        List<String> typeParameters = new ArrayList<>();

        if (ctx != null) {
            for (JavaParser.TypeParameterContext typeParam : ctx.typeParameter()) {
                String identifier = typeParam.IDENTIFIER().getText();
                String extendedByPostfix = (typeParam.typeBound() != null)
                        ? " extends " + typeParam.typeBound().getText() : "";
                typeParameters.add(identifier + extendedByPostfix);
            }
        }
        builder.typeParameters(typeParameters);
    }

    private static void applyTypeParameters(MethodLanguageElement.Builder builder,
                                            JavaParser.TypeParametersContext ctx) {
        List<String> typeParameters = new ArrayList<>();

        if (ctx != null) {
            for (JavaParser.TypeParameterContext typeParam : ctx.typeParameter()) {
                String identifier = typeParam.IDENTIFIER().getText();
                String extendedByPostfix = (typeParam.typeBound() != null)
                        ? " extends " + typeParam.typeBound().getText() : "";
                typeParameters.add(identifier + extendedByPostfix);
            }
        }
        builder.typeParameters(typeParameters);
    }

    private static void applyMethodModifiers(MethodLanguageElement.Builder methodBuilder,
                                             JavaParser.ModifierContext ctx) {
        if (ctx.NATIVE() != null) {
            methodBuilder.nativeModifier(true);
        }

        if (ctx.SYNCHRONIZED() != null) {
            methodBuilder.synchronizedModifier(true);
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

    private static void applyInstanceVariableModifiers(
            InstanceVariableLanguageElement.Builder variableBuilder,
            JavaParser.ModifierContext ctx) {
        // Ignored modifiers: NATIVE, SYNCHRONIZED
        JavaParser.ClassOrInterfaceModifierContext mods = ctx.classOrInterfaceModifier();

        if (mods != null) {
            if (mods.PUBLIC() != null) {
                variableBuilder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mods.PRIVATE() != null) {
                variableBuilder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mods.PROTECTED() != null) {
                variableBuilder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mods.FINAL() != null) {
                variableBuilder.finalModifier(true);
            } else if (mods.STATIC() != null) {
                variableBuilder.staticModifier(true);
            }
            // Ignored modifiers: STRICTFP, ABSTRACT
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

    private static ClassLanguageElement parseClass(JavaParser.TypeDeclarationContext ctx,
                                                   List<LanguageElement> topLevelElements) {
        List<String> superClasses = new ArrayList<>();
        JavaParser.ClassDeclarationContext dec = ctx.classDeclaration();
        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName);

        // Class modifiers
        applyClassModifiers(builder, ctx.classOrInterfaceModifier());

        // Type parameters
        applyTypeParameters(builder, dec.typeParameters());

        // Implemented interfaces
        parseImplemented(superClasses, dec.typeList());

        // Extended class
        JavaParser.TypeTypeContext extendedClass = dec.typeType();

        if (extendedClass != null) {
            superClasses.add(extendedClass.getText());
        }
        builder.superClasses(superClasses);

        // Methods
        if (dec.classBody() != null) {
            for (JavaParser.ClassBodyDeclarationContext classBody : dec.classBody().classBodyDeclaration()) {
                JavaParser.MemberDeclarationContext memberDec = classBody.memberDeclaration();
                JavaParser.MethodDeclarationContext method = memberDec.methodDeclaration();
                JavaParser.GenericMethodDeclarationContext genericMethod = memberDec.genericMethodDeclaration();
                JavaParser.FieldDeclarationContext field = memberDec.fieldDeclaration();

                if (method != null) { // method
                    MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                            method.typeTypeOrVoid(), classBody.modifier(),
                            method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);
                    // TODO finish (incl. parse method body)
                    topLevelElements.add(methodBuilder.build());
                } else if (genericMethod != null) { // generic method
                    method = genericMethod.methodDeclaration();
                    MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                            method.typeTypeOrVoid(), classBody.modifier(),
                            method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);
                    // TODO finish (incl. parse method body)
                    applyTypeParameters(methodBuilder, genericMethod.typeParameters());
                    topLevelElements.add(methodBuilder.build());
                } else if (field != null) { // field
                    String identifierType = field.typeType().getText();

                    for (JavaParser.VariableDeclaratorContext decl
                            : field.variableDeclarators().variableDeclarator()) {
                        JavaParser.VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                        String identifier = identifierCtx.IDENTIFIER().getText();

                        for (int i = 0; i < identifierCtx.LBRACK().size(); i++) { // XXX what is this even for?
                            identifier += "[]";
                        }

                        InstanceVariableLanguageElement.Builder variableBuilder
                                = InstanceVariableLanguageElement.Builder
                                .allFalse(DEF, identifier)
                                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                                .identifierType(identifierType);

                        if (decl.variableInitializer() != null) {
                            variableBuilder.valueExpression(decl.variableInitializer().getText());
                        }

                        for (JavaParser.ModifierContext mods : classBody.modifier()) {
                            applyInstanceVariableModifiers(variableBuilder, mods);
                        }
                        topLevelElements.add(variableBuilder.build());

                    }}
            }
        }
        // TODO finish
        return builder.build();
    }

    private static ClassLanguageElement parseInterface(JavaParser.TypeDeclarationContext ctx,
                                                       List<LanguageElement> topLevelElements) {
        List<String> superClasses = new ArrayList<>();
        JavaParser.InterfaceDeclarationContext dec = ctx.interfaceDeclaration();
        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                .interfaceModifier(true);

        // Class modifiers
        applyClassModifiers(builder, ctx.classOrInterfaceModifier());

        // Type parameters
        applyTypeParameters(builder, dec.typeParameters());

        // Extended interfaces
        parseImplemented(superClasses, dec.typeList());
        builder.superClasses(superClasses);

        // Methods
        for (JavaParser.InterfaceBodyDeclarationContext intBody : dec.interfaceBody().interfaceBodyDeclaration()) {
            JavaParser.InterfaceMemberDeclarationContext memberDec = intBody.interfaceMemberDeclaration();
            JavaParser.InterfaceMethodDeclarationContext method = memberDec.interfaceMethodDeclaration();
            JavaParser.GenericInterfaceMethodDeclarationContext genericMethod
                    = memberDec.genericInterfaceMethodDeclaration();
            JavaParser.ConstDeclarationContext constDecl = memberDec.constDeclaration();

            if (method != null) { // method
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(),
                        method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);

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
                        methodBuilder.defaultModifier(true);
                    }
                }
                topLevelElements.add(methodBuilder.build());
            } else if (genericMethod != null) { // generic method
                method = genericMethod.interfaceMethodDeclaration();
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(),
                        method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);
                applyTypeParameters(methodBuilder, genericMethod.typeParameters());
                topLevelElements.add(methodBuilder.build());
            } else if (constDecl != null) { // constant
                String identifierType = constDecl.typeType().getText();

                for (JavaParser.ConstantDeclaratorContext decl : constDecl.constantDeclarator()) {
                    String identifier = decl.IDENTIFIER().getText();

                    for (int i = 0; i < decl.LBRACK().size(); i++) { // XXX what is this even for?
                        identifier += "[]";
                    }
                    String valueExpression = decl.variableInitializer().getText();

                    InstanceVariableLanguageElement.Builder variableBuilder
                            = InstanceVariableLanguageElement.Builder
                            .allFalse(DEF, identifier)
                            .identifierType(identifierType)
                            .valueExpression(valueExpression);

                    for (JavaParser.ModifierContext mods : intBody.modifier()) {
                        applyInstanceVariableModifiers(variableBuilder, mods);
                    }
                    topLevelElements.add(variableBuilder.build());
                }
            }
        }
        // TODO finish
        return builder.build();
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

        LanguageElement rootElement = null;
        List<LanguageElement> rootsChildrenElements = new ArrayList<>();

        // Generate node
        if (ctx.classDeclaration() != null) { // class
            rootElement = parseClass(ctx, rootsChildrenElements);
        } else if (ctx.interfaceDeclaration() != null) { // interface
            rootElement = parseInterface(ctx, rootsChildrenElements);
        }

        // Create root
        rootNode = new Node(rootElement);

        // Append methods to root
        for (LanguageElement method : rootsChildrenElements) {
            rootNode.addNode(new Node(method));
        }
    }

    public Node getRootNode() {
        return rootNode;
    }
}
