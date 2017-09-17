package org.qmul.csar.code;

import grammars.java8pt.JavaParser;
import grammars.java8pt.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.*;
import org.qmul.csar.lang.Expression.UnitExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grammars.java8pt.JavaParser.*;
import static org.qmul.csar.query.CsarQuery.Type.DEF;
import static org.qmul.csar.query.CsarQuery.Type.USE;

public final class JavaCodeTreeGenerator extends JavaParserBaseListener {

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

    private static List<Node> parseAnnotations(List<VariableModifierContext> mods) {
        List<Node> annotations = new ArrayList<>();

        for (VariableModifierContext vm : mods) {
            if (vm.annotation() != null) {
                annotations.add(parseAnnotationUse(vm.annotation()));
            }
        }
        return annotations;
    }

    private static List<Node> parseAnnotations2(List<AnnotationContext> annotations) {
        List<Node> out = new ArrayList<>();

        for (AnnotationContext annotation : annotations) {
            out.add(parseAnnotationUse(annotation));
        }
        return out;
    }

    private static List<Node> parseAnnotations3(List<ModifierContext> mods) {
        List<Node> annotations = new ArrayList<>();

        for (ModifierContext mod : mods) {
            if (mod.classOrInterfaceModifier() != null && mod.classOrInterfaceModifier().annotation() != null) {
                annotations.add(parseAnnotationUse(mod.classOrInterfaceModifier().annotation()));
            }
        }
        return annotations;
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
            List<VariableLanguageElement> outVariableLanguageElements) {
        if (ctx == null)
            return;
        for (FormalParameterContext p : ctx.formalParameter()) { // regular args
            String name = p.variableDeclaratorId().getText();
            String type = p.typeType().getText();
            boolean finalModifier = containsFinal(p.variableModifier());
            outVariableLanguageElements.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type)
                    .finalModifier(finalModifier)
                    .build());
            outParams.add(new Parameter(type, Optional.of(name), Optional.of(finalModifier),
                    parseAnnotations(p.variableModifier())));
        }

        if (ctx.lastFormalParameter() != null) { // varargs
            LastFormalParameterContext last = ctx.lastFormalParameter();
            String name = last.variableDeclaratorId().getText();
            String type = last.typeType().getText();
            boolean finalModifier = containsFinal(last.variableModifier());
            outVariableLanguageElements.add(new VariableLanguageElement.Builder(DEF, VariableType.PARAM, name)
                    .identifierType(type + "...")
                    .finalModifier(finalModifier)
                    .build());
            outParams.add(new Parameter(type + "...", Optional.of(name), Optional.of(finalModifier),
                    parseAnnotations(last.variableModifier())));
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
            } else if (mod.annotation() != null) {
                builder.annotation(parseAnnotationUse(mod.annotation()));
            }
        }
    }

    private static void applyAnnotationModifiers(AnnotationLanguageElement.Builder builder,
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
            } else if (mod.STATIC() != null) {
                builder.staticModifier(true);
            } else if (mod.STRICTFP() != null) {
                builder.strictfpModifier(true);
            } else if (mod.annotation() != null) {
                builder.annotation(parseAnnotationUse(mod.annotation()));
            }
        }
    }

    private static void applyEnumModifiers(EnumLanguageElement.Builder builder,
            List<ClassOrInterfaceModifierContext> mods) {
        for (ClassOrInterfaceModifierContext mod : mods) {
            if (mod.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mod.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mod.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mod.FINAL() != null) {
                builder.finalModifier(true);
            } else if (mod.STATIC() != null) {
                builder.staticModifier(true);
            } else if (mod.STRICTFP() != null) {
                builder.strictfpModifier(true);
            } else if (mod.annotation() != null) {
                builder.annotation(parseAnnotationUse(mod.annotation()));
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
            } else if (mods.annotation() != null) {
                builder.annotation(parseAnnotationUse(mods.annotation()));
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
        List<String> throwsList = throwsCtx == null ? new ArrayList<>() : parseThrows(throwsCtx);
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
        List<String> throwsList = throwsCtx == null ? new ArrayList<>() : parseThrows(throwsCtx);
        return builder.parameterCount(paramIdentifiers.size())
                .parameters(paramIdentifiers)
                .thrownExceptions(throwsList);
    }

    private static Expression parseExpression(ExpressionContext ctx) {
        if (ctx.primary() != null) {
            PrimaryContext primary = ctx.primary();
            UnitExpression.Type type;

            if (primary.LPAREN() != null && primary.RPAREN() != null) { // LPAREN expression RPAREN
                return new Expression.ParenthesisExpression(parseExpression(primary.expression()));
            } else if (primary.DOT() != null && primary.CLASS() != null) { // class reference: typeTypeOrVoid DOT CLASS
                type = UnitExpression.Type.CLASS_REFERENCE;
            } else if (primary.methodReference() != null) { // method reference: methodReference
                type = UnitExpression.Type.METHOD_REFERENCE;
            } else if (primary.THIS() != null) { // THIS
                type = UnitExpression.Type.THIS;
            } else if (primary.SUPER() != null) { // SUPER
                type = UnitExpression.Type.SUPER;
            } else if (primary.literal() != null) { // literal
                type = UnitExpression.Type.LITERAL;
            } else if (primary.IDENTIFIER() != null) { // IDENTIFIER
                type = UnitExpression.Type.IDENTIFIER;
            } else { // nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
                type = (primary.arguments() != null) ? UnitExpression.Type.THIS_CALL
                        : UnitExpression.Type.SUPER_CALL;
            }
            return new UnitExpression(type, primary.getText());
        }

        // Binary operations
        if (ctx.bop != null) {
            try {
                Expression.BinaryOperation op = Expression.BinaryOperation.forSymbol(ctx.bop.getText());
                Expression left = parseExpression(ctx.expression(0));

                if (op == Expression.BinaryOperation.DOT) {
                    Expression right = null;

                    if (ctx.IDENTIFIER() != null) {
                        right = new UnitExpression(UnitExpression.Type.IDENTIFIER, ctx.IDENTIFIER().getText());
                    } else if (ctx.THIS() != null) {
                        right = new UnitExpression(UnitExpression.Type.THIS, ctx.THIS().getText());
                    } else if (ctx.SUPER() != null) { // SUPER superSuffix
                        right = new UnitExpression(UnitExpression.Type.SUPER_CALL, ctx.superSuffix().getText());
                    } else if (ctx.NEW() != null) { // NEW nonWildcardTypeArguments? innerCreator
                        String typeArg = (ctx.nonWildcardTypeArguments() == null) ? ""
                                : ctx.nonWildcardTypeArguments().getText();
                        right = new UnitExpression(UnitExpression.Type.NEW, typeArg + ctx.innerCreator().getText());
                    } else if (ctx.explicitGenericInvocation() != null) { // explicitGenericInvocation
                        ExplicitGenericInvocationContext invokCtx = ctx.explicitGenericInvocation();
                        UnitExpression.Type type = (invokCtx.explicitGenericInvocationSuffix().SUPER() == null)
                                ? UnitExpression.Type.METHOD_CALL : UnitExpression.Type.SUPER_CALL;
                        right = new UnitExpression(type, invokCtx.getText());
                    }
                    return new Expression.BinaryExpression(left, op, right);
                } else if (op == Expression.BinaryOperation.QUESTION) {
                    Expression valueIfTrue = parseExpression(ctx.expression(1));
                    Expression valueIfFalse = parseExpression(ctx.expression(2));
                    return new Expression.TernaryExpression(left, valueIfTrue, valueIfFalse);
                } else if (op == Expression.BinaryOperation.INSTANCE_OF) {
                    Expression unitExpression = new UnitExpression(UnitExpression.Type.TYPE,
                            ctx.typeType().getText());
                    return new Expression.BinaryExpression(left, op, unitExpression);
                }
                Expression right = parseExpression(ctx.expression(1));
                return new Expression.BinaryExpression(left, op, right);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Lambda expression
        if (ctx.lambdaExpression() != null) {
            LambdaParametersContext params = ctx.lambdaExpression().lambdaParameters();
            LambdaBodyContext body = ctx.lambdaExpression().lambdaBody();

            // Parameters
            String identifier = null;
            List<String> identifiers = new ArrayList<>();
            List<Parameter> parameters = new ArrayList<>();

            if (params.LPAREN() == null && params.IDENTIFIER().size() == 1) {
                identifier = params.IDENTIFIER(0).getText();
            } else if (params.LPAREN() == null && params.IDENTIFIER().size() > 0) {
                for (TerminalNode ident : params.IDENTIFIER()) {
                    identifiers.add(ident.getText());
                }
            } else {
                parseParameters(params.formalParameterList(), parameters, new ArrayList<>());
            }

            // Body
            Expression expr = null;
            List<Node> nodes = new ArrayList<>();

            if (body.expression() != null) {
                expr = parseExpression(body.expression());
            } else {
                for (BlockStatementContext block : body.block().blockStatement()) {
                    try {
                        nodes.add(new Node(parseBlock(block)));
                    } catch (UnsupportedOperationException ignored) {
                    }
                }
            }

            if (identifier != null) {
                return new Expression.LambdaExpression(identifier, expr != null ? expr : nodes);
            } else if (parameters.size() > 0) {
                return new Expression.LambdaExpression(parameters, expr != null ? expr : nodes);
            } else {
                return new Expression.LambdaExpression(identifiers, expr != null ? expr : nodes);
            }
        }

        // Define class or array
        if (ctx.NEW() != null) {
            CreatorContext creator = ctx.creator();
            String name = creator.createdName().getText();

            if (creator.classCreatorRest() != null) { // nonWildcardTypeArguments? createdName classCreatorRest
                ClassCreatorRestContext rest = creator.classCreatorRest();

                // Arguments
                List<Expression> arguments = new ArrayList<>();
                ArgumentsContext args = rest.arguments();

                if (args.expressionList() != null) {
                    for (ExpressionContext exprCtx : args.expressionList().expression()) {
                        arguments.add(parseExpression(exprCtx));
                    }
                }

                // Type arguments
                boolean hasTypeArguments = creator.nonWildcardTypeArguments() != null;
                List<String> typeArgs = new ArrayList<>();

                if (hasTypeArguments && creator.nonWildcardTypeArguments().typeList() != null) {
                    for (TypeTypeContext t : creator.nonWildcardTypeArguments().typeList().typeType()) {
                        typeArgs.add(t.getText());
                    }
                }

                ClassLanguageElement.Builder builder = ClassLanguageElement.Builder.allFalse(DEF, name)
                        .local(true)
                        .inner(false);
                List<Node> children = rest.classBody() != null
                        ? parseClassBodyDeclaration(rest.classBody().classBodyDeclaration()) : new ArrayList<>();
                Node classNode = new Node(builder.build());
                children.forEach(classNode::addNode);
                return new Expression.InstantiateClass(arguments, classNode, typeArgs, hasTypeArguments);
            } else { // createdName arrayCreatorRest
                ArrayCreatorRestContext rest = creator.arrayCreatorRest();
                List<Expression> contents = new ArrayList<>();

                if (rest.arrayInitializer() != null) { // squareBrackets squareBrackets* arrayInitializer
                    for (SquareBracketsContext sq : rest.squareBrackets()) {
                        contents.add(new Expression.SquareBracketsExpression(Optional.empty()));
                    }
                    List<Expression> expressions = new ArrayList<>();

                    for (VariableInitializerContext initCtx : rest.arrayInitializer().variableInitializer()) {
                        expressions.add(parseVariableInitializerExpression(initCtx));
                    }
                    contents.add(new Expression.ArrayExpression(expressions));
                } else { // squareBracketsExpression squareBracketsExpression* squareBrackets*
                    for (SquareBracketsExpressionContext sqExprCtx : rest.squareBracketsExpression()) {
                        Optional<Expression> expr = Optional.of(parseExpression(sqExprCtx.expression()));
                        contents.add(new Expression.SquareBracketsExpression(expr));
                    }

                    for (SquareBracketsContext sq : rest.squareBrackets()) {
                        contents.add(new Expression.SquareBracketsExpression(Optional.empty()));
                    }
                }
                return new Expression.ArrayDefinitionExpression(contents);
            }
        }

        // Cast
        if (ctx.LPAREN() != null && ctx.RPAREN() != null && ctx.typeType() != null & ctx.expression().size() == 1) {
            return new Expression.CastExpression(ctx.typeType().getText(), parseExpression(ctx.expression(0)));
        }

        // Array access
        if (ctx.LBRACK() != null && ctx.RBRACK() != null) {
            Expression array = parseExpression(ctx.expression(0));
            Expression index = parseExpression(ctx.expression(1));
            return new Expression.ArrayAccessExpression(array, index);
        }

        // Prefix
        if (ctx.prefix != null) {
            try {
                Expression.Prefix prefix = Expression.Prefix.forSymbol(ctx.prefix.getText());
                Expression expr = parseExpression(ctx.expression(0));
                return new Expression.PrefixedExpression(expr, prefix);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Postfix
        if (ctx.postfix != null) {
            try {
                Expression.Postfix postfix = Expression.Postfix.forSymbol(ctx.postfix.getText());
                Expression expr = parseExpression(ctx.expression(0));
                return new Expression.PostfixedExpression(expr, postfix);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Fall-back: expression LPAREN expressionList? RPAREN
        if (ctx.expression().size() > 0) {
            Expression method = parseExpression(ctx.expression(0));
            List<Expression> parameters = new ArrayList<>();

            if (ctx.expressionList() != null) {
                for (ExpressionContext ectx : ctx.expressionList().expression()) {
                    try {
                        parameters.add(parseExpression(ectx));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            return new Expression.MethodCallExpression(method, parameters);
        }
        throw new IllegalArgumentException("invalid context");
    }

    private static Statement parseBlock(BlockStatementContext st) {
        // local variable declaration
        LocalVariableDeclarationContext local = st.localVariableDeclaration();

        if (local != null) {
            return parseLocalVariables(local);
        }

        // statement
        StatementContext statement = st.statement();

        if (statement != null) {
            return parseStatement(statement);
        }

        // Fall-back: type declaration
        InnerTypeDeclarationContext innerTypeDeclaration = st.innerTypeDeclaration();

        // Generate node and add node
        if (innerTypeDeclaration.classDeclaration() != null) { // class
            return parseClass(innerTypeDeclaration.classOrInterfaceModifier(),
                    innerTypeDeclaration.classDeclaration(), true, false);
        } else { // interface
            return parseInterface(innerTypeDeclaration.classOrInterfaceModifier(),
                    innerTypeDeclaration.interfaceDeclaration(), true, false);
        }
    }

    private static SwitchControlFlowLanguageElement.SwitchLabel parseSwitchLabel(SwitchLabelContext ctx) {
        if (ctx.constantExpression != null) {
            return new SwitchControlFlowLanguageElement.SwitchLabel(
                    Optional.of(parseExpression(ctx.constantExpression)), Optional.empty());
        } else if (ctx.enumConstantName != null) {
            return new SwitchControlFlowLanguageElement.SwitchLabel(Optional.empty(),
                    Optional.of(ctx.enumConstantName.getText()));
        } else { // default
            return new SwitchControlFlowLanguageElement.SwitchLabel(Optional.empty(), Optional.of("default"));
        }
    }

    private static VariableLanguageElement.VariableLanguageElements parseLocalVariables(
            LocalVariableDeclarationContext local) {
        List<VariableLanguageElement> elements = new ArrayList<>();
        boolean finalModifier = containsFinal(local.variableModifier());
        List<Node> annotations = parseAnnotations(local.variableModifier());
        String identifierType = local.typeType().getText();

        for (VariableDeclaratorContext decl : local.variableDeclarators().variableDeclarator()) {
            VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
            String identifier = identifierCtx.IDENTIFIER().getText();

            for (int i = 0; i < identifierCtx.LBRACK().size(); i++) {
                identifier += "[]";
            }

            VariableLanguageElement.Builder variableBuilder
                    = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifier)
                    .identifierType(identifierType)
                    .finalModifier(finalModifier)
                    .annotation(annotations);

            if (decl.variableInitializer() != null) {
                variableBuilder.valueExpression(parseVariableInitializerExpression(decl.variableInitializer()));
            }
            elements.add(variableBuilder.build());
        }
        return new VariableLanguageElement.VariableLanguageElements(elements);
    }

    private static Expression parseVariableInitializerExpression(VariableInitializerContext ctx) {
        if (ctx.expression() != null) {
            return parseExpression(ctx.expression());
        } else { // array initializer
            List<Expression> expressions = new ArrayList<>();

            for (VariableInitializerContext initCtx : ctx.arrayInitializer().variableInitializer()) {
                expressions.add(parseVariableInitializerExpression(initCtx));
            }
            return new Expression.ArrayExpression(expressions);
        }
    }

    private static Statement parseStatement(StatementContext ctx) {
        try {
            if (ctx.THROW() != null) {
                return new ThrowControlFlowLanguageElement(parseExpression(ctx.expression(0)));
            } else if (ctx.SWITCH() != null) {
                Expression condition = new Expression.ParenthesisExpression(
                        parseExpression(ctx.parExpression().expression()));
                List<Statement> elements = new ArrayList<>();

                for (SwitchBlockStatementGroupContext group : ctx.switchBlockStatementGroup()) {
                    for (BlockStatementContext block : group.blockStatement()) {
                        elements.add(parseBlock(block));
                    }

                    for (SwitchLabelContext label : group.switchLabel()) {
                        elements.add(parseSwitchLabel(label));
                    }
                }

                for (SwitchLabelContext label : ctx.switchLabel()) {
                    elements.add(parseSwitchLabel(label));
                }
                return new SwitchControlFlowLanguageElement(condition, elements);
            } else if (ctx.FOR() != null) {
                Statement statement = parseStatement(ctx.statement(0));

                if (ctx.forControl().enhancedForControl() != null) { // for-each
                    EnhancedForControlContext en = ctx.forControl().enhancedForControl();

                    // Variable
                    boolean finalModifier = containsFinal(en.variableModifier());
                    List<Node> annotations = parseAnnotations(en.variableModifier());
                    String identifierType = en.typeType().getText();

                    VariableDeclaratorIdContext identifierCtx = en.variableDeclaratorId();
                    String identifierName = identifierCtx.IDENTIFIER().getText();

                    for (int i = 0; i < identifierCtx.LBRACK().size(); i++) {
                        identifierName += "[]";
                    }

                    VariableLanguageElement variable
                            = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifierName)
                            .finalModifier(finalModifier)
                            .identifierType(identifierType)
                            .annotation(annotations)
                            .build();
                    // Collection
                    Expression collection = parseExpression(en.expression());
                    return new ForEachControlFlowLanguageElement(variable, collection, statement);
                } else { // for-loop
                    ForControlContext forCtx = ctx.forControl();

                    // For init
                    ForInitContext init = forCtx.forInit();
                    Optional<VariableLanguageElement.VariableLanguageElements> initVariables = Optional.empty();
                    List<Expression> initExpressions = new ArrayList<>();

                    if (init != null) {
                        if (init.localVariableDeclaration() != null) {
                            initVariables = Optional.of(parseLocalVariables(init.localVariableDeclaration()));
                        } else {
                            for (ExpressionContext ectx : init.expressionList().expression()) {
                                try {
                                    initExpressions.add(parseExpression(ectx));
                                } catch (IllegalArgumentException ignored) {
                                }
                            }
                        }
                    }

                    // Condition
                    Optional<Expression> condition = forCtx.expression() != null
                            ? Optional.of(parseExpression(forCtx.expression())) : Optional.empty();

                    // Update
                    ExpressionListContext exprList = forCtx.forUpdate;
                    List<Expression> updateExpressions = new ArrayList<>();

                    if (exprList != null) {
                        for (ExpressionContext ectx : exprList.expression()) {
                            try {
                                updateExpressions.add(parseExpression(ectx));
                            } catch (IllegalArgumentException ignored) {
                            }
                        }
                    }
                    return new ForControlFlowLanguageElement(initVariables, initExpressions, condition,
                            updateExpressions);
                }
            } else if (ctx.IF() != null) {
                Expression condition = new Expression.ParenthesisExpression(
                        parseExpression(ctx.parExpression().expression()));
                Statement statement = parseStatement(ctx.statement(0));
                Optional<Statement> elseStatement = ctx.statement().size() == 2
                        ? Optional.of(parseStatement(ctx.statement(1))) : Optional.empty();
                return new IfControlFlowLanguageElement(condition, statement, elseStatement);
            } else if (ctx.TRY() != null) {
                if (ctx.resourceSpecification() != null) { // try w/ resources
                    // Try
                    List<VariableLanguageElement> resources = new ArrayList<>();

                    for (ResourceContext res : ctx.resourceSpecification().resources().resource()) {
                        boolean finalModifier = containsFinal(res.variableModifier());
                        List<Node> annotations = parseAnnotations(res.variableModifier());
                        String identifierType = res.classOrInterfaceType().getText();

                        VariableDeclaratorIdContext identifierCtx = res.variableDeclaratorId();
                        String identifierName = identifierCtx.IDENTIFIER().getText();

                        for (int i = 0; i < identifierCtx.LBRACK().size(); i++) {
                            identifierName += "[]";
                        }
                        Expression value = parseExpression(res.expression());

                        VariableLanguageElement variable
                                = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifierName)
                                .finalModifier(finalModifier)
                                .identifierType(identifierType)
                                .valueExpression(value)
                                .annotation(annotations)
                                .build();
                        resources.add(variable);
                    }

                    List<Statement> block = new ArrayList<>();

                    for (BlockStatementContext bsc : ctx.block().blockStatement()) {
                        block.add(parseBlock(bsc));
                    }

                    // Catch
                    List<CatchControlFlowLanguageElement> catchElements = new ArrayList<>();

                    for (CatchClauseContext catchClauseCtx : ctx.catchClause()) {
                        boolean finalModifier = containsFinal(catchClauseCtx.variableModifier());
                        List<Node> annotations = parseAnnotations(catchClauseCtx.variableModifier());
                        String identifierName = catchClauseCtx.IDENTIFIER().getText();
                        String identifierType = catchClauseCtx.catchType().getText();
                        VariableLanguageElement variable
                                = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifierName)
                                .identifierType(identifierType)
                                .finalModifier(finalModifier)
                                .annotation(annotations)
                                .build();

                        List<Statement> catchBlock = new ArrayList<>();

                        for (BlockStatementContext bsc : catchClauseCtx.block().blockStatement()) {
                            catchBlock.add(parseBlock(bsc));
                        }
                        catchElements.add(new CatchControlFlowLanguageElement(variable, catchBlock));
                    }

                    // Finally
                    List<Statement> finallyBlock = new ArrayList<>();

                    if (ctx.finallyBlock() != null) {
                        for (BlockStatementContext bsc : ctx.finallyBlock().block().blockStatement()) {
                            finallyBlock.add(parseBlock(bsc));
                        }
                    }
                    return new TryResourcesControlFlowLanguageElement(resources, block, catchElements, finallyBlock);
                } else { // try-catch
                    // Try
                    List<Statement> block = new ArrayList<>();

                    for (BlockStatementContext bsc : ctx.block().blockStatement()) {
                        block.add(parseBlock(bsc));
                    }

                    // Catch
                    List<CatchControlFlowLanguageElement> catchElements = new ArrayList<>();

                    for (CatchClauseContext catchClauseCtx : ctx.catchClause()) {
                        boolean finalModifier = containsFinal(catchClauseCtx.variableModifier());
                        List<Node> annotations = parseAnnotations(catchClauseCtx.variableModifier());
                        String identifierName = catchClauseCtx.IDENTIFIER().getText();
                        String identifierType = catchClauseCtx.catchType().getText();
                        VariableLanguageElement variable
                                = new VariableLanguageElement.Builder(DEF, VariableType.LOCAL, identifierName)
                                .identifierType(identifierType)
                                .finalModifier(finalModifier)
                                .annotation(annotations)
                                .build();

                        List<Statement> catchBlock = new ArrayList<>();

                        for (BlockStatementContext bsc : catchClauseCtx.block().blockStatement()) {
                            catchBlock.add(parseBlock(bsc));
                        }
                        catchElements.add(new CatchControlFlowLanguageElement(variable, catchBlock));
                    }

                    // Finally
                    List<Statement> finallyBlock = new ArrayList<>();

                    if (ctx.finallyBlock() != null) {
                        for (BlockStatementContext bsc : ctx.finallyBlock().block().blockStatement()) {
                            finallyBlock.add(parseBlock(bsc));
                        }
                    }
                    return new TryControlFlowLanguageElement(block, catchElements, finallyBlock);
                }
            } else if (ctx.RETURN() != null) {
                Optional<Expression> expr = (ctx.expression().size() == 1)
                        ? Optional.of(parseExpression(ctx.expression(0))) : Optional.empty();
                return new ReturnControlFlowLanguageElement(expr);
            } else if (ctx.statementExpression != null) { // statementExpression=expression SEMI
                Optional<Expression> expr = Optional.of(parseExpression(ctx.statementExpression));
                return new Expression.SemiColonTerminatedExpression(expr);
            } else if (ctx.BREAK() != null) {
                Optional<String> identifier = getTextOrEmpty(ctx.IDENTIFIER());
                return new BreakControlFlowLanguageElement(identifier);
            } else if (ctx.CONTINUE() != null) {
                Optional<String> identifier = getTextOrEmpty(ctx.IDENTIFIER());
                return new ContinueControlFlowLanguageElement(identifier);
            } else if (ctx.ASSERT() != null) {
                Expression expr = parseExpression(ctx.expression(0));
                Optional<Expression> errorMsg = (ctx.expression().size() == 2)
                        ? Optional.of(parseExpression(ctx.expression(1))) : Optional.empty();
                return new AssertControlFlowLanguageElement(expr, errorMsg);
            } else if (ctx.DO() != null) {
                Expression condition = new Expression.ParenthesisExpression(
                        parseExpression(ctx.parExpression().expression()));
                Statement statement = parseStatement(ctx.statement(0));
                return new DoWhileControlFlowLanguageElement(condition, statement);
            } else if (ctx.SYNCHRONIZED() != null) {
                Expression target = new Expression.ParenthesisExpression(
                        parseExpression(ctx.parExpression().expression()));
                List<Statement> statements = new ArrayList<>();

                for (BlockStatementContext bsc : ctx.block().blockStatement()) {
                    statements.add(parseBlock(bsc));
                }
                return new SynchronizedControlFlowLanguageElement(target, new BlockLanguageElement(statements));
            } else if (ctx.WHILE() != null && ctx.DO() == null) {
                Expression condition = new Expression.ParenthesisExpression(
                        parseExpression(ctx.parExpression().expression()));
                Statement statement = parseStatement(ctx.statement(0));
                return new WhileControlFlowLanguageElement(condition, statement);
            } else if (ctx.blockLabel != null) {
                List<Statement> statements = new ArrayList<>();

                for (BlockStatementContext bsc : ctx.blockLabel.blockStatement()) {
                    statements.add(parseBlock(bsc));
                }
                return new BlockLanguageElement(statements);
            } else if (ctx.identifierLabel != null) {
                Statement statement = parseStatement(ctx.statement(0));
                return new LabelControlFlowLanguageElement(ctx.IDENTIFIER().getText(), statement);
            } else { // semi colon
                return new Expression.SemiColonExpression();
            }
        } catch (IllegalArgumentException ignored) {
        }
        throw new UnsupportedOperationException();
    }


    private static Node parseAnnotationDefinition(List<ClassOrInterfaceModifierContext> modifiers,
            AnnotationTypeDeclarationContext dec) {
        return parseAnnotationDefinition(modifiers, dec, false);
    }

    private static Node parseAnnotationDefinition(List<ClassOrInterfaceModifierContext> modifiers,
            AnnotationTypeDeclarationContext dec, boolean inner) {
        String identifierName = dec.IDENTIFIER().getText();
        AnnotationLanguageElement.Builder builder = AnnotationLanguageElement.Builder.allFalse(DEF, identifierName)
                .inner(inner);
        applyAnnotationModifiers(builder, modifiers);
        Node node = new Node(builder.build());

        for (AnnotationTypeElementDeclarationContext ctx
                : dec.annotationTypeBody().annotationTypeElementDeclaration()) {
            AnnotationTypeElementRestContext typeDecl = ctx.annotationTypeElementRest();
            AnnotationMethodOrConstantRestContext multiDec = typeDecl.annotationMethodOrConstantRest();
            ClassDeclarationContext clazzDec = typeDecl.classDeclaration();
            InterfaceDeclarationContext intDec = typeDecl.interfaceDeclaration();
            EnumDeclarationContext enumDec = typeDecl.enumDeclaration();
            AnnotationTypeDeclarationContext anonType = typeDecl.annotationTypeDeclaration();

            if (clazzDec != null) {
                Node classNode = parseClass(toClassOrInterfaceModifierContexts(ctx.modifier()), clazzDec, false, true);
                node.addNode(classNode);
            } else if (intDec != null) {
                Node interfaceNode = parseInterface(toClassOrInterfaceModifierContexts(ctx.modifier()), intDec, false,
                        true);
                node.addNode(interfaceNode);
            } else if (enumDec != null) {
                Node enumNode = parseEnum(toClassOrInterfaceModifierContexts(ctx.modifier()), enumDec, false, true);
                node.addNode(enumNode);
            } else if (anonType != null) {
                Node anonDefNode = parseAnnotationDefinition(toClassOrInterfaceModifierContexts(ctx.modifier()),
                        anonType);
                node.addNode(anonDefNode);
            } else { // multiDec
                AnnotationMethodRestContext methodRest = multiDec.annotationMethodRest();
                String identifierType = typeDecl.typeType().getText();

                if (methodRest != null) {
                    String variableIdentifierName = methodRest.IDENTIFIER().getText();

                    if (methodRest.defaultValue() != null) {
                        Node annotationNode = parseAnnotationElementValue(Optional.of(variableIdentifierName),
                                Optional.of(identifierType), methodRest.defaultValue().elementValue());
                        node.addNode(annotationNode);
                    } else {
                        List<Node> annotations = parseAnnotations3(ctx.modifier());
                        node.addNode(new Node(new AnnotationElementValue(Optional.of(variableIdentifierName),
                                Optional.of(identifierType), Optional.empty(), annotations)));
                    }
                } else { // annotationConstantRest
                    VariableDeclaratorsContext decs = multiDec.annotationConstantRest().variableDeclarators();

                    for (VariableDeclaratorContext decl : decs.variableDeclarator()) {
                        VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                        String identifier = identifierCtx.IDENTIFIER().getText();

                        for (int i = 0; i < identifierCtx.LBRACK().size(); i++) {
                            identifier += "[]";
                        }
                        InstanceVariableLanguageElement.Builder variableBuilder
                                = InstanceVariableLanguageElement.Builder.allFalse(DEF, identifier)
                                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                                .identifierType(identifierType);

                        if (decl.variableInitializer() != null) {
                            variableBuilder.valueExpression(parseVariableInitializerExpression(decl.variableInitializer()));
                        }
                        ctx.modifier().forEach(mod -> applyInstanceVariableModifiers(variableBuilder, mod));
                        node.addNode(new Node(variableBuilder.build()));
                    }
                }
            }
        }
        return node;
    }

    private static Node parseAnnotationUse(AnnotationContext ctx) {
        String identifierName = ctx.qualifiedName().getText();
        Node annotationNode = new Node(AnnotationLanguageElement.Builder.allFalse(USE, identifierName).build());

        if (ctx.elementValuePairs() != null) {
            for (ElementValuePairContext pair : ctx.elementValuePairs().elementValuePair()) {
                annotationNode.addNode(parseAnnotationElementValue(Optional.of(pair.IDENTIFIER().getText()),
                        Optional.empty(), pair.elementValue()));
            }
        } else if (ctx.elementValue() != null) {
            annotationNode.addNode(parseAnnotationElementValue(Optional.empty(), Optional.empty(), ctx.elementValue()));
        }
        return annotationNode;
    }

    private static Node parseAnnotationElementValue(Optional<String> identifierName, Optional<String> identifierType,
            ElementValueContext ctx) {
        if (ctx.expression() != null) {
            return new Node(new AnnotationElementValue(identifierName, identifierType,
                    Optional.of(parseExpression(ctx.expression())), new ArrayList<>()));
        } else if (ctx.annotation() != null) {
            return parseAnnotationUse(ctx.annotation());
        } else { // elementValueArrayInitializer
            List<Expression> expressions = new ArrayList<>();

            for (ElementValueContext valCtx : ctx.elementValueArrayInitializer().elementValue()) {
                Node n = parseAnnotationElementValue(identifierName, Optional.empty(), valCtx);
                expressions.add(((AnnotationElementValue)n.getData()).getValue().get());
            }
            return new Node(new AnnotationElementValue(identifierName, identifierType,
                    Optional.of(new Expression.ArrayExpression(expressions)), new ArrayList<>()));
        }
    }

    private static Optional<String> getTextOrEmpty(TerminalNode node) {
        return node != null ? Optional.of(node.getText()) : Optional.empty();
    }

    private static Node parseEnum(TypeDeclarationContext ctx) {
        return parseEnum(ctx.classOrInterfaceModifier(), ctx.enumDeclaration(), false, false);
    }

    private static Node parseEnum(TypeDeclarationContext ctx, boolean local, boolean inner) {
        return parseEnum(ctx.classOrInterfaceModifier(), ctx.enumDeclaration(), local, inner);
    }

    private static Node parseEnum(List<JavaParser.ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            EnumDeclarationContext dec, boolean local, boolean inner) {
        List<String> superClasses = new ArrayList<>();

        String identifierName = dec.IDENTIFIER().getText();
        EnumLanguageElement.Builder builder = EnumLanguageElement.Builder.allFalse(DEF, identifierName)
                .inner(inner);

        // Class modifiers
        applyEnumModifiers(builder, classOrInterfaceModifierContexts);

        // Implemented interfaces
        applyImplemented(superClasses, dec.typeList());

        List<Node> children = new ArrayList<>();

        // Enum constants
        List<Node> constants = dec.enumConstants() != null
                ? parseEnumConstantDeclaration(dec.enumConstants().enumConstant()) : new ArrayList<>();
        children.addAll(constants);

        // Enum body declarations
        List<Node> body = dec.enumBodyDeclarations() != null
                ? parseClassBodyDeclaration(dec.enumBodyDeclarations().classBodyDeclaration()) : new ArrayList<>();
        children.addAll(body);

        // Create and return node
        Node root = new Node(builder.build());
        children.forEach(root::addNode);
        return root;
    }

    private static List<Node> parseEnumConstantDeclaration(List<EnumConstantContext> enumConstantContexts) {
        List<Node> children = new ArrayList<>();

        for (EnumConstantContext constant : enumConstantContexts) { // annotation* IDENTIFIER arguments? classBody?
            List<Node> annotations = parseAnnotations2(constant.annotation());
            String identifierName = constant.IDENTIFIER().getText();

            // Arguments
            List<Expression> arguments = new ArrayList<>();
            ArgumentsContext args = constant.arguments();

            if (args != null && args.expressionList() != null) {
                for (ExpressionContext exprCtx : args.expressionList().expression()) {
                    arguments.add(parseExpression(exprCtx));
                }
            }

            // classBody
            List<Node> body = constant.classBody() != null
                    ? parseClassBodyDeclaration(constant.classBody().classBodyDeclaration()) : new ArrayList<>();
            Node constantNode = new Node(new EnumConstantLanguageElement(identifierName, arguments, body, annotations));
            body.forEach(constantNode::addNode);
            children.add(constantNode);
        }
        return children;
    }

    private static Node parseClass(TypeDeclarationContext ctx) {
        return parseClass(ctx.classOrInterfaceModifier(), ctx.classDeclaration(), false, false);
    }

    private static Node parseClass(TypeDeclarationContext ctx, boolean local, boolean inner) {
        return parseClass(ctx.classOrInterfaceModifier(), ctx.classDeclaration(), local, inner);
    }

    private static Node parseClass(List<JavaParser.ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            ClassDeclarationContext dec, boolean local, boolean inner) {
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
        List<Node> children = dec.classBody() != null
                ? parseClassBodyDeclaration(dec.classBody().classBodyDeclaration()) : new ArrayList<>();

        // Create and return node
        Node root = new Node(builder.build());
        children.forEach(root::addNode);
        return root;
    }

    private static List<Node> parseClassBodyDeclaration(List<ClassBodyDeclarationContext> classBodyDeclarationContexts) {
        List<Node> children = new ArrayList<>();

        for (ClassBodyDeclarationContext classBody : classBodyDeclarationContexts) {
            // Static block
            if (classBody.STATIC() != null) {
                List<Statement> block = new ArrayList<>();

                for (BlockStatementContext bsc : classBody.block().blockStatement()) {
                    block.add(parseBlock(bsc));
                }
                children.add(new Node(new StaticStatementBlock(block)));
                continue;
            }

            // Member declarations
            MemberDeclarationContext m = classBody.memberDeclaration();

            if (m == null)
                continue;
            MethodDeclarationContext method = m.methodDeclaration();
            GenericMethodDeclarationContext genericMethod = m.genericMethodDeclaration();
            ConstructorDeclarationContext constructor = m.constructorDeclaration();
            GenericConstructorDeclarationContext genericConstructor = m.genericConstructorDeclaration();
            ClassDeclarationContext innerClass = m.classDeclaration();
            InterfaceDeclarationContext innerInterface = m.interfaceDeclaration();
            EnumDeclarationContext innerEnum = m.enumDeclaration();
            FieldDeclarationContext field = m.fieldDeclaration();

            if (method != null) { // method
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), classBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);

                // Parse body
                Node methodNode = new Node(methodBuilder.build());

                if (method.methodBody().block() != null) {
                    for (BlockStatementContext st : method.methodBody().block().blockStatement()) {
                        try {
                            methodNode.addNode(new Node(parseBlock(st)));
                        } catch (UnsupportedOperationException ignored) {
                        }
                    }
                }
                children.add(methodNode);
            } else if (genericMethod != null) { // generic method
                method = genericMethod.methodDeclaration();
                MethodLanguageElement.Builder methodBuilder = parseMethodSkeleton(method.IDENTIFIER(),
                        method.typeTypeOrVoid(), classBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false);

                // Type parameters
                methodBuilder.typeParameters(parseTypeParameters(genericMethod.typeParameters()));

                // Parse body
                Node methodNode = new Node(methodBuilder.build());

                if (method.methodBody().block() != null) {
                    for (BlockStatementContext st : method.methodBody().block().blockStatement()) {
                        try {
                            methodNode.addNode(new Node(parseBlock(st)));
                        } catch (UnsupportedOperationException ignored) {
                        }
                    }
                }
                children.add(methodNode);
            } else if (field != null) { // field
                String identifierType = field.typeType().getText();

                for (VariableDeclaratorContext decl : field.variableDeclarators().variableDeclarator()) {
                    VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                    String identifier = identifierCtx.IDENTIFIER().getText();

                    for (int i = 0; i < identifierCtx.LBRACK().size(); i++) {
                        identifier += "[]";
                    }
                    InstanceVariableLanguageElement.Builder variableBuilder
                            = InstanceVariableLanguageElement.Builder.allFalse(DEF, identifier)
                            .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                            .identifierType(identifierType);

                    if (decl.variableInitializer() != null) {
                        variableBuilder.valueExpression(parseVariableInitializerExpression(decl.variableInitializer()));
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
                        try {
                            constructorNode.addNode(new Node(parseBlock(st)));
                        } catch (UnsupportedOperationException ignored) {
                        }
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
                        try {
                            constructorNode.addNode(new Node(parseBlock(st)));
                        } catch (UnsupportedOperationException ignored) {
                        }
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
            } else if (innerEnum != null) { // inner enum
                Node node = parseEnum(toClassOrInterfaceModifierContexts(classBody.modifier()), innerEnum, false, true);
                children.add(node);
            }
        }
        return children;
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
            EnumDeclarationContext enumDec = memberDec.enumDeclaration();
            AnnotationTypeDeclarationContext anonDecl = memberDec.annotationTypeDeclaration();

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
                    } else if (mods.annotation() != null) {
                        methodBuilder.annotation(parseAnnotationUse(mods.annotation()));
                    }
                }

                // Parse method body
                MethodBodyContext methodBody = method.methodBody();
                Node methodNode = new Node(methodBuilder.build());

                if (methodBody.block() != null) {
                    for (BlockStatementContext bsc : methodBody.block().blockStatement()) {
                        try {
                            methodNode.addNode(new Node(parseBlock(bsc)));
                        } catch (UnsupportedOperationException ignored) {
                        }
                    }
                }
                children.add(methodNode);
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

                    for (int i = 0; i < decl.LBRACK().size(); i++) {
                        identifier += "[]";
                    }

                    InstanceVariableLanguageElement.Builder variableBuilder
                            = InstanceVariableLanguageElement.Builder.allFalse(DEF, identifier)
                            .identifierType(identifierType)
                            .valueExpression(parseVariableInitializerExpression(decl.variableInitializer()));
                    intBody.modifier().forEach(mod -> applyInstanceVariableModifiers(variableBuilder, mod));
                    children.add(new Node(variableBuilder.build()));
                }
            } else if (classDec != null) { // inner class
                Node node = parseClass(toClassOrInterfaceModifierContexts(intBody.modifier()), classDec, false, true);
                children.add(node);
            } else if (enumDec != null) { // inner enum
                Node node = parseEnum(toClassOrInterfaceModifierContexts(intBody.modifier()), enumDec, false, true);
                children.add(node);
            } else if (anonDecl != null) { // inner annotation def
                Node node = parseAnnotationDefinition(toClassOrInterfaceModifierContexts(intBody.modifier()),
                        anonDecl, true);
                children.add(node);
            }
        }

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

        // Generate node and add node
        if (ctx.classDeclaration() != null) { // class
            rootNode = parseClass(ctx);
        } else if (ctx.interfaceDeclaration() != null) { // interface
            rootNode = parseInterface(ctx);
        } else if (ctx.enumDeclaration() != null) { // enum
            rootNode = parseEnum(ctx);
        } else { // annotation
            rootNode = parseAnnotationDefinition(ctx.classOrInterfaceModifier(), ctx.annotationTypeDeclaration());

        }
    }

    public Node getRootNode() {
        return rootNode;
    }
}
