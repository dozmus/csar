package org.qmul.csar.code;

import grammars.java8pt.JavaParser;
import grammars.java8pt.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grammars.java8pt.JavaParser.*;
import static org.qmul.csar.query.CsarQuery.Type.DEF;

public final class JavaCodeTreeGenerator extends JavaParserBaseListener {

    // TODO set variableExpression properly, its losing spaces between new and commas etc.
    // TODO class/method LE may need to be extended to be given more information - could just be bad parser code tho

    private Node rootNode;
    /**
     * This is used to make sure {@link #enterTypeDeclaration(TypeDeclarationContext)} is only called once,
     * since it sets the {@link #rootNode} property. Other type declarations are parsed elsewhere.
     */
    private boolean processedMainTypeDecl = false;

    /**
     * Collects only the classOrInterfaceModifier elements.
     *
     * @param mods
     * @return
     */
    private static List<ClassOrInterfaceModifierContext> toClassOrInterfaceModifierContexts(
            List<ModifierContext> mods) {
        List<ClassOrInterfaceModifierContext> output = new ArrayList<>();

        for (ModifierContext ctx : mods) {
            if (ctx.classOrInterfaceModifier() != null) {
                output.add(ctx.classOrInterfaceModifier());
            }
        }
        return output;
    }

    private static boolean containsFinal(List<VariableModifierContext> mods) {
        for (VariableModifierContext vm : mods) {
            if (vm.FINAL() != null) {
                return true;
            }
        }
        return false;
    }

    private static void parseParameters(FormalParameterListContext ctx, List<Parameter> outParams,
            List<VariableLanguageElement> params) {
        if (ctx == null)
            return;
        for (FormalParameterContext p : ctx.formalParameter()) { // regular args
            String name = p.variableDeclaratorId().getText();
            String type = p.typeType().getText();
            boolean finalModifier = containsFinal(p.variableModifier());
            params.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type)
                    .finalModifier(finalModifier)
                    .build());
            outParams.add(new Parameter(type, Optional.of(name), Optional.of(finalModifier)));
        }

        if (ctx.lastFormalParameter() != null) { // varargs
            LastFormalParameterContext last = ctx.lastFormalParameter();
            String name = last.variableDeclaratorId().getText();
            String type = last.typeType().getText();
            boolean finalModifier = containsFinal(last.variableModifier());
            params.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type + "...")
                    .finalModifier(finalModifier)
                    .build());
            outParams.add(new Parameter(type + "...", Optional.of(name), Optional.of(finalModifier)));
        }
    }

    private static void applyClassModifiers(ClassLanguageElement.Builder builder,
            List<ClassOrInterfaceModifierContext> mods) {
        for (ClassOrInterfaceModifierContext mod : mods) {
            if (mod.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mod.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mod.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mod.ABSTRACT() != null) {
                builder.abstractModifier(true);
            } else if (mod.FINAL() != null) {
                builder.finalModifier(true);
            } else if (mod.STATIC() != null) {
                builder.staticModifier(true);
            } else if (mod.STRICTFP() != null) {
                builder.strictfpModifier(true);
            }
        }
    }

    private static void applyImplemented(List<String> superClasses, TypeListContext ctx) {
        if (ctx == null)
            return;
        for (TypeTypeContext t : ctx.typeType()) {
            superClasses.add(t.getText());
        }
    }

    private static List<String> parseThrows(QualifiedNameListContext ctx) {
        List<String> throwsList = new ArrayList<>();

        for (QualifiedNameContext q : ctx.qualifiedName()) {
            for (TerminalNode identifier : q.IDENTIFIER()) {
                throwsList.add(identifier.getText());
            }
        }
        return throwsList;
    }

    private static List<String> parseTypeParameters(TypeParametersContext ctx) {
        List<String> typeParameters = new ArrayList<>();

        if (ctx != null) {
            for (TypeParameterContext typeParam : ctx.typeParameter()) {
                String identifier = typeParam.IDENTIFIER().getText();
                String typeParamPostfix = (typeParam.typeBound() != null)
                        ? " extends " + typeParam.typeBound().getText() : "";
                typeParameters.add(identifier + typeParamPostfix);
            }
        }
        return typeParameters;
    }

    private static void applyMethodModifiers(MethodLanguageElement.Builder builder, ModifierContext mod) {
        if (mod.NATIVE() != null) {
            builder.nativeModifier(true);
        }

        if (mod.SYNCHRONIZED() != null) {
            builder.synchronizedModifier(true);
        }
        ClassOrInterfaceModifierContext mods = mod.classOrInterfaceModifier();

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
            ModifierContext mod) {
        // Ignored modifiers: NATIVE, SYNCHRONIZED
        ClassOrInterfaceModifierContext mods = mod.classOrInterfaceModifier();

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
            TypeTypeOrVoidContext type, List<JavaParser.ModifierContext> modifiers,
            FormalParameterListContext parameterCtx, QualifiedNameListContext throwsCtx, boolean overridden) {
        String identifierName = identifier.getText();
        MethodLanguageElement.Builder builder = MethodLanguageElement.Builder.allFalse(DEF, identifierName)
                .overridden(overridden)
                .returnType(type.getText());

        // Modifiers
        modifiers.forEach(mod -> applyMethodModifiers(builder, mod));

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

    private static ConstructorLanguageElement.Builder parseConstructorSkeleton(TerminalNode identifier,
            List<JavaParser.ModifierContext> modifiers, FormalParameterListContext parameterCtx,
            QualifiedNameListContext throwsCtx) {
        ConstructorLanguageElement.Builder builder = new ConstructorLanguageElement.Builder(DEF, identifier.getText());

        // Modifiers
        for (ModifierContext mod : modifiers) {
            ClassOrInterfaceModifierContext mods = mod.classOrInterfaceModifier();

            if (mods == null)
                continue;

            if (mods.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mods.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mods.PROTECTED() != null) {
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

    private static void applyBodyStatement(Node parent, BlockStatementContext st) {
        // local variable declaration
        LocalVariableDeclarationContext local = st.localVariableDeclaration();

        if (local != null) {
            boolean finalModifier = containsFinal(local.variableModifier());
            String identifierType = local.typeType().getText();

            for (VariableDeclaratorContext decl : local.variableDeclarators()
                    .variableDeclarator()) {
                VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
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
        StatementContext statement = st.statement();

        if (statement != null) {
            // TODO impl
        }

        // type declaration
        TypeDeclarationContext typeDeclaration = st.typeDeclaration();

        if (typeDeclaration != null) { // TODO test
            // NOTE this is unfinished
            // Check if node type is handled
            if (typeDeclaration.classDeclaration() == null && typeDeclaration.interfaceDeclaration() == null) {
                // TODO error: stop generating and tell the codeparser to skip the file
                System.err.println("Unhandled file type");
                return;
            }

            // Generate node and add node
            Node currentNode = null;

            if (typeDeclaration.classDeclaration() != null) { // class
                currentNode = parseClass(typeDeclaration, true, false);
            } else if (typeDeclaration.interfaceDeclaration() != null) { // interface
                currentNode = parseInterface(typeDeclaration, true, false);
            }
            parent.addNode(currentNode);
        }
    }

    private static Node parseClass(TypeDeclarationContext ctx) {
        return parseClass(ctx.classOrInterfaceModifier(), ctx.classDeclaration(), false, false);
    }

    private static Node parseClass(TypeDeclarationContext ctx, boolean local, boolean inner) {
        return parseClass(ctx.classOrInterfaceModifier(), ctx.classDeclaration(), local, inner);
    }

    private static Node parseClass(List<JavaParser.ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            ClassDeclarationContext dec, boolean local, boolean inner) {
        List<Node> children = new ArrayList<>();
        List<String> superClasses = new ArrayList<>();

        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                .local(local)
                .inner(inner);

        // Class modifiers
        applyClassModifiers(builder, classOrInterfaceModifierContexts);

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Implemented interfaces
        applyImplemented(superClasses, dec.typeList());

        // Extended class
        TypeTypeContext extendedClass = dec.typeType();

        if (extendedClass != null) {
            superClasses.add(extendedClass.getText());
        }
        builder.superClasses(superClasses);

        // Methods
        if (dec.classBody() != null) {
            for (ClassBodyDeclarationContext classBody : dec.classBody().classBodyDeclaration()) {
                MemberDeclarationContext m = classBody.memberDeclaration();

                if (m == null)
                    continue;
                MethodDeclarationContext method = m.methodDeclaration();
                GenericMethodDeclarationContext genericMethod = m.genericMethodDeclaration();
                ConstructorDeclarationContext constructor = m.constructorDeclaration();
                GenericConstructorDeclarationContext genericConstructor = m.genericConstructorDeclaration();
                ClassDeclarationContext innerClass = m.classDeclaration();
                InterfaceDeclarationContext innerInterface = m.interfaceDeclaration();
                FieldDeclarationContext field = m.fieldDeclaration();

                if (method != null) { // method
                    MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                            method.typeTypeOrVoid(), classBody.modifier(),
                            method.formalParameters().formalParameterList(), method.qualifiedNameList(), false);

                    // Parse body
                    Node methodNode = new Node(methodBuilder.build());

                    if (method.methodBody().block() != null) {
                        for (BlockStatementContext st : method.methodBody().block().blockStatement()) {
                            applyBodyStatement(methodNode, st);
                        }
                    }
                    children.add(methodNode);
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
                        for (BlockStatementContext st : method.methodBody().block().blockStatement()) {
                            applyBodyStatement(methodNode, st);
                        }
                    }
                    children.add(methodNode);
                } else if (field != null) { // field
                    String identifierType = field.typeType().getText();

                    for (VariableDeclaratorContext decl
                            : field.variableDeclarators().variableDeclarator()) {
                        VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                        String identifier = identifierCtx.IDENTIFIER().getText();

                        for (int i = 0; i < identifierCtx.LBRACK().size(); i++) { // XXX what is this even for?
                            identifier += "[]";
                        }
                        InstanceVariableLanguageElement.Builder variableBuilder
                                = InstanceVariableLanguageElement.Builder.allFalse(DEF, identifier)
                                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                                .identifierType(identifierType);

                        if (decl.variableInitializer() != null) {
                            variableBuilder.valueExpression(decl.variableInitializer().getText());
                        }
                        classBody.modifier().forEach(mod -> applyInstanceVariableModifiers(variableBuilder, mod));
                        children.add(new Node(variableBuilder.build()));
                    }
                } else if (constructor != null) { // constructor
                    ConstructorLanguageElement.Builder consBuilder = parseConstructorSkeleton(
                            constructor.IDENTIFIER(), classBody.modifier(),
                            constructor.formalParameters().formalParameterList(), constructor.qualifiedNameList());

                    // Parse body
                    Node constructorNode = new Node(consBuilder.build());

                    if (constructor.block() != null) {
                        for (BlockStatementContext st : constructor.block().blockStatement()) {
                            applyBodyStatement(constructorNode, st);
                        }
                    }
                    children.add(constructorNode);
                } else if (genericConstructor != null) { // generic constructor
                    ConstructorDeclarationContext cons = genericConstructor.constructorDeclaration();
                    ConstructorLanguageElement.Builder consBuilder = parseConstructorSkeleton(cons.IDENTIFIER(),
                            classBody.modifier(),
                            cons.formalParameters().formalParameterList(), cons.qualifiedNameList());

                    // Type parameters
                    consBuilder.typeParameters(parseTypeParameters(genericConstructor.typeParameters()));

                    // Parse body
                    Node constructorNode = new Node(consBuilder.build());

                    if (genericConstructor.constructorDeclaration().block() != null) {
                        for (BlockStatementContext st
                                : genericConstructor.constructorDeclaration().block().blockStatement()) {
                            applyBodyStatement(constructorNode, st);
                        }
                    }
                    children.add(constructorNode);
                } else if (innerClass != null) { // inner class
                    Node node = parseClass(toClassOrInterfaceModifierContexts(classBody.modifier()), innerClass, false,
                            true);
                    children.add(node);
                } else if (innerInterface != null) { // inner interface
                    Node node = parseInterface(toClassOrInterfaceModifierContexts(classBody.modifier()), innerInterface,
                            false, true);
                    children.add(node);
                }
            }
        }
        // TODO finish

        // Create and return node
        Node root = new Node(builder.build());
        children.forEach(root::addNode);
        return root;
    }

    private static Node parseInterface(TypeDeclarationContext ctx) {
        return parseInterface(ctx.classOrInterfaceModifier(), ctx.interfaceDeclaration(), false, false);
    }

    private static Node parseInterface(TypeDeclarationContext ctx, boolean local, boolean inner) {
        return parseInterface(ctx.classOrInterfaceModifier(), ctx.interfaceDeclaration(), local, inner);
    }

    private static Node parseInterface(
            List<JavaParser.ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            InterfaceDeclarationContext dec, boolean local, boolean inner) {
        List<Node> children = new ArrayList<>();
        List<String> superClasses = new ArrayList<>();

        String identifierName = dec.IDENTIFIER().getText();
        ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, identifierName)
                .interfaceModifier(true)
                .local(local)
                .inner(inner);

        // Class modifiers
        applyClassModifiers(builder, classOrInterfaceModifierContexts);

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Extended interfaces
        applyImplemented(superClasses, dec.typeList());
        builder.superClasses(superClasses);

        // Body
        for (InterfaceBodyDeclarationContext intBody : dec.interfaceBody().interfaceBodyDeclaration()) {
            InterfaceMemberDeclarationContext memberDec = intBody.interfaceMemberDeclaration();
            InterfaceMethodDeclarationContext method = memberDec.interfaceMethodDeclaration();
            GenericInterfaceMethodDeclarationContext genericMethod
                    = memberDec.genericInterfaceMethodDeclaration();
            ClassDeclarationContext classDec = memberDec.classDeclaration();
            ConstDeclarationContext constDecl = memberDec.constDeclaration();

            if (method != null) { // method
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);

                for (InterfaceMethodModifierContext mods : method.interfaceMethodModifier()) {
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
                children.add(new Node(methodBuilder.build()));
            } else if (genericMethod != null) { // generic method
                method = genericMethod.interfaceMethodDeclaration();
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), intBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);
                methodBuilder.typeParameters(parseTypeParameters(genericMethod.typeParameters()));
                children.add(new Node(methodBuilder.build()));
            } else if (constDecl != null) { // constant
                String identifierType = constDecl.typeType().getText();

                for (ConstantDeclaratorContext decl : constDecl.constantDeclarator()) {
                    String identifier = decl.IDENTIFIER().getText();

                    for (int i = 0; i < decl.LBRACK().size(); i++) { // XXX what is this even for?
                        identifier += "[]";
                    }
                    String valueExpression = decl.variableInitializer().getText();

                    InstanceVariableLanguageElement.Builder variableBuilder
                            = InstanceVariableLanguageElement.Builder.allFalse(DEF, identifier)
                            .identifierType(identifierType)
                            .valueExpression(valueExpression);
                    intBody.modifier().forEach(mod -> applyInstanceVariableModifiers(variableBuilder, mod));
                    children.add(new Node(variableBuilder.build()));
                }
            } else if (classDec != null) { // inner class
                Node node = parseClass(toClassOrInterfaceModifierContexts(intBody.modifier()), classDec, false, true);
                children.add(node);
            }
        }
        // TODO finish

        // Create and return node
        Node root = new Node(builder.build());
        children.forEach(root::addNode);
        return root;
    }

    @Override
    public void enterPackageDeclaration(PackageDeclarationContext ctx) {
        // TODO impl
    }

    @Override
    public void enterImportDeclaration(ImportDeclarationContext ctx) {
        // TODO impl
    }

    @Override
    public void enterTypeDeclaration(TypeDeclarationContext ctx) {
        // Check if the main type in this class was processed already
        if (!processedMainTypeDecl) {
            processedMainTypeDecl = true;
        } else {
            return;
        }

        // NOTE this method is unfinished
        // Check if node type is handled
        if (ctx.classDeclaration() == null && ctx.interfaceDeclaration() == null) {
            throw new RuntimeException("unhandled top level element");
        }

        // Generate node and add node
        if (ctx.classDeclaration() != null) { // class
            rootNode = parseClass(ctx);
        } else if (ctx.interfaceDeclaration() != null) { // interface
            rootNode = parseInterface(ctx);
        }
    }

    public Node getRootNode() {
        return rootNode;
    }
}
