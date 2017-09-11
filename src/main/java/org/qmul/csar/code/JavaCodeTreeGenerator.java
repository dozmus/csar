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

    // TODO set variableExpression properly, its losing spaces between new and commas etc.
    // TODO class/method LE may need to be extended to be given more information - could just be bad parser code tho

    private Node rootNode;
    /**
     * This is used to make sure {@link #enterTypeDeclaration(JavaParser.TypeDeclarationContext)} is only called once,
     * since it sets the {@link #rootNode} property. Other type declarations are parsed elsewhere.
     */
    private boolean processedMainTypeDecl = false;

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

    private static void applyImplemented(List<String> superClasses, JavaParser.TypeListContext ctx) {
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

    private static List<String> parseTypeParameters(JavaParser.TypeParametersContext ctx) {
        List<String> typeParameters = new ArrayList<>();

        if (ctx != null) {
            for (JavaParser.TypeParameterContext typeParam : ctx.typeParameter()) {
                String identifier = typeParam.IDENTIFIER().getText();
                String typeParamPostfix = (typeParam.typeBound() != null)
                        ? " extends " + typeParam.typeBound().getText() : "";
                typeParameters.add(identifier + typeParamPostfix);
            }
        }
        return typeParameters;
    }

    private static void applyMethodModifiers(MethodLanguageElement.Builder builder, JavaParser.ModifierContext ctx) {
        if (ctx.NATIVE() != null) {
            builder.nativeModifier(true);
        }

        if (ctx.SYNCHRONIZED() != null) {
            builder.synchronizedModifier(true);
        }
        JavaParser.ClassOrInterfaceModifierContext mods = ctx.classOrInterfaceModifier();

        if (mods != null) {
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

    private static void applyInstanceVariableModifiers(InstanceVariableLanguageElement.Builder builder,
                                                       JavaParser.ModifierContext ctx) {
        // Ignored modifiers: NATIVE, SYNCHRONIZED
        JavaParser.ClassOrInterfaceModifierContext mods = ctx.classOrInterfaceModifier();

        if (mods != null) {
            if (mods.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mods.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mods.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mods.FINAL() != null) {
                builder.finalModifier(true);
            } else if (mods.STATIC() != null) {
                builder.staticModifier(true);
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
        MethodLanguageElement.Builder builder = MethodLanguageElement.Builder.allFalse(DEF, identifierName)
                .overridden(overridden)
                .returnType(type.getText());

        // Modifiers
        for (JavaParser.ModifierContext modifier : modifiers) {
            applyMethodModifiers(builder, modifier);
        }

        // Parameters
        List<Parameter> paramIdentifiers = new ArrayList<>();
        List<VariableLanguageElement> params = new ArrayList<>();
        parseParameters(parameterCtx, paramIdentifiers, params);

        // Throws list
        List<String> throwsList = (throwsCtx == null) ? new ArrayList<>() : parseThrows(throwsCtx);
        return builder
                .parameterCount(paramIdentifiers.size())
                .parameters(paramIdentifiers)
                .thrownExceptions(throwsList);
    }

    private static ConstructorLanguageElement.Builder parseConstructorSkeleton(TerminalNode identifier,
                                                                               List<JavaParser.ModifierContext> modifiers,
                                                                               JavaParser.FormalParameterListContext parameterCtx,
                                                                               JavaParser.QualifiedNameListContext throwsCtx) {
        ConstructorLanguageElement.Builder builder = new ConstructorLanguageElement
                .Builder(DEF, identifier.getText());

        // Modifiers
        for (JavaParser.ModifierContext modifier : modifiers) {
            JavaParser.ClassOrInterfaceModifierContext mod = modifier.classOrInterfaceModifier();

            if (mod == null)
                continue;

            if (mod.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mod.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mod.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            }
        }

        // Parameters
        List<Parameter> paramIdentifiers = new ArrayList<>();
        List<VariableLanguageElement> params = new ArrayList<>();
        parseParameters(parameterCtx, paramIdentifiers, params);

        // Throws list
        List<String> throwsList = (throwsCtx == null) ? new ArrayList<>() : parseThrows(throwsCtx);
        return builder.parameterCount(paramIdentifiers.size())
                .parameters(paramIdentifiers)
                .thrownExceptions(throwsList);
    }

    private static void applyBodyStatement(Node parent, JavaParser.BlockStatementContext st) {
        // local variable declaration
        JavaParser.LocalVariableDeclarationContext local = st.localVariableDeclaration();

        if (local != null) {
            boolean finalModifier = false;

            for (JavaParser.VariableModifierContext mod : local.variableModifier()) {
                if (mod.FINAL() != null) {
                    finalModifier = true;
                    break;
                }
            }
            String identifierType = local.typeType().getText();

            for (JavaParser.VariableDeclaratorContext decl : local.variableDeclarators()
                    .variableDeclarator()) {
                JavaParser.VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                String identifier = identifierCtx.IDENTIFIER().getText();

                for (int i = 0; i < identifierCtx.LBRACK().size(); i++) { // XXX what is this even for?
                    identifier += "[]";
                }

                VariableLanguageElement.Builder variableBuilder
                        = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifier)
                        .identifierType(identifierType)
                        .finalModifier(finalModifier);

                if (decl.variableInitializer() != null) {
                    variableBuilder.valueExpression(decl.variableInitializer().getText());
                }
                parent.addNode(new Node(variableBuilder.build()));
            }
        }

        // statement
        JavaParser.StatementContext statement = st.statement();

        if (statement != null) {
            // TODO impl
        }

        // type declaration
        JavaParser.TypeDeclarationContext typeDeclaration = st.typeDeclaration();

        if (typeDeclaration != null) { // TODO test
            // NOTE this is unfinished
            // Check if node type is handled
            if (typeDeclaration.classDeclaration() == null && typeDeclaration.interfaceDeclaration() == null) {
                // TODO error: stop generating and tell the codeparser to skip the file
                System.err.println("Unhandled file type");
                return;
            }

            ClassLanguageElement element = null;
            List<Node> childrenNodes = new ArrayList<>();

            // Generate node
            if (typeDeclaration.classDeclaration() != null) { // class
                element = parseClass(typeDeclaration, childrenNodes, true);
            } else if (typeDeclaration.interfaceDeclaration() != null) { // interface
                element = parseInterface(typeDeclaration, childrenNodes, true);
            }

            // Add to statements
            Node currentNode = new Node(element);
            parent.addNode(currentNode);

            // Append methods to root
            for (Node child : childrenNodes) {
                currentNode.addNode(child);
            }
        }
    }

    private static ClassLanguageElement parseClass(JavaParser.TypeDeclarationContext ctx,
                                                   List<Node> topLevelElements) {
        return parseClass(ctx, topLevelElements, false);
    }

    private static ClassLanguageElement parseClass(JavaParser.TypeDeclarationContext ctx,
                                                   List<Node> topLevelElements, boolean local) {
        List<String> superClasses = new ArrayList<>();
        JavaParser.ClassDeclarationContext dec = ctx.classDeclaration();
        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                .local(local);

        // Class modifiers
        applyClassModifiers(builder, ctx.classOrInterfaceModifier());

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Implemented interfaces
        applyImplemented(superClasses, dec.typeList());

        // Extended class
        JavaParser.TypeTypeContext extendedClass = dec.typeType();

        if (extendedClass != null) {
            superClasses.add(extendedClass.getText());
        }
        builder.superClasses(superClasses);

        // Methods
        if (dec.classBody() != null) {
            for (JavaParser.ClassBodyDeclarationContext classBody : dec.classBody().classBodyDeclaration()) {
                JavaParser.MemberDeclarationContext m = classBody.memberDeclaration();

                if (m == null)
                    continue;
                JavaParser.MethodDeclarationContext method = m.methodDeclaration();
                JavaParser.GenericMethodDeclarationContext genericMethod = m.genericMethodDeclaration();
                JavaParser.ConstructorDeclarationContext constructor = m.constructorDeclaration();
                JavaParser.GenericConstructorDeclarationContext genericConstructor = m.genericConstructorDeclaration();
                JavaParser.FieldDeclarationContext field = m.fieldDeclaration();

                if (method != null) { // method
                    MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                            method.typeTypeOrVoid(), classBody.modifier(),
                            method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);

                    // Parse body
                    Node methodNode = new Node(methodBuilder.build());

                    if (method.methodBody().block() != null) {
                        for (JavaParser.BlockStatementContext st : method.methodBody().block().blockStatement()) {
                            applyBodyStatement(methodNode, st);
                        }
                    }
                    topLevelElements.add(methodNode);
                } else if (genericMethod != null) { // generic method
                    method = genericMethod.methodDeclaration();
                    MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                            method.typeTypeOrVoid(), classBody.modifier(),
                            method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);

                    // Type parameters
                    methodBuilder.typeParameters(parseTypeParameters(genericMethod.typeParameters()));

                    // Parse body
                    Node methodNode = new Node(methodBuilder.build());

                    if (method.methodBody().block() != null) {
                        for (JavaParser.BlockStatementContext st : method.methodBody().block().blockStatement()) {
                            applyBodyStatement(methodNode, st);
                        }
                    }
                    topLevelElements.add(methodNode);
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
                        topLevelElements.add(new Node(variableBuilder.build()));
                    }
                } else if (constructor != null) { // constructor
                    ConstructorLanguageElement.Builder consBuilder = parseConstructorSkeleton(
                            constructor.IDENTIFIER(), classBody.modifier(),
                            constructor.formalParameters().formalParameterList(), constructor.qualifiedNameList());

                    // Parse body
                    Node constructorNode = new Node(consBuilder.build());

                    if (constructor.block() != null) {
                        for (JavaParser.BlockStatementContext st : constructor.block().blockStatement()) {
                            applyBodyStatement(constructorNode, st);
                        }
                    }
                    topLevelElements.add(constructorNode);
                } else if (genericConstructor != null) { // generic constructor
                    JavaParser.ConstructorDeclarationContext cons = genericConstructor.constructorDeclaration();
                    ConstructorLanguageElement.Builder consBuilder = parseConstructorSkeleton(cons.IDENTIFIER(),
                            classBody.modifier(),
                            cons.formalParameters().formalParameterList(), cons.qualifiedNameList());

                    // Type parameters
                    consBuilder.typeParameters(parseTypeParameters(genericConstructor.typeParameters()));

                    // Parse body
                    Node constructorNode = new Node(consBuilder.build());

                    if (genericConstructor.constructorDeclaration().block() != null) {
                        for (JavaParser.BlockStatementContext st
                                : genericConstructor.constructorDeclaration().block().blockStatement()) {
                            applyBodyStatement(constructorNode, st);
                        }
                    }
                    topLevelElements.add(constructorNode);
                }
            }
        }
        // TODO finish
        return builder.build();
    }

    private static ClassLanguageElement parseInterface(JavaParser.TypeDeclarationContext ctx,
                                                       List<Node> topLevelElements) {
        return parseInterface(ctx, topLevelElements, false);
    }

    private static ClassLanguageElement parseInterface(JavaParser.TypeDeclarationContext ctx,
                                                       List<Node> topLevelElements, boolean local) {
        List<String> superClasses = new ArrayList<>();
        JavaParser.InterfaceDeclarationContext dec = ctx.interfaceDeclaration();
        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                .interfaceModifier(true)
                .local(local);

        // Class modifiers
        applyClassModifiers(builder, ctx.classOrInterfaceModifier());

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Extended interfaces
        applyImplemented(superClasses, dec.typeList());
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
                // TODO parse body if a default method
                topLevelElements.add(new Node(methodBuilder.build()));
            } else if (genericMethod != null) { // generic method
                method = genericMethod.interfaceMethodDeclaration();
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(),
                        method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);
                methodBuilder.typeParameters(parseTypeParameters(genericMethod.typeParameters()));
                topLevelElements.add(new Node(methodBuilder.build()));
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
                    topLevelElements.add(new Node(variableBuilder.build()));
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
        if (!processedMainTypeDecl) {
            processedMainTypeDecl = true;
        } else {
            return;
        }

        // NOTE this method is unfinished
        // Check if node type is handled
        if (ctx.classDeclaration() == null && ctx.interfaceDeclaration() == null) {
            // TODO error: stop generating and tell the codeparser to skip the file
            System.err.println("Unhandled file type");
            return;
        }

        LanguageElement rootElement = null;
        List<Node> childrenNodes = new ArrayList<>();

        // Generate node
        if (ctx.classDeclaration() != null) { // class
            rootElement = parseClass(ctx, childrenNodes);
        } else if (ctx.interfaceDeclaration() != null) { // interface
            rootElement = parseInterface(ctx, childrenNodes);
        }

        // Create root
        rootNode = new Node(rootElement);

        // Append methods to root
        for (Node child : childrenNodes) {
            rootNode.addNode(child);
        }
    }

    public Node getRootNode() {
        return rootNode;
    }
}
