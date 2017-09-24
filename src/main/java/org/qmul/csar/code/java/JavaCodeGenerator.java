package org.qmul.csar.code.java;

import grammars.java8pt.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.code.java.expression.*;
import org.qmul.csar.code.java.statement.*;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static grammars.java8pt.JavaParser.*;

/**
 * A Java8 code parser.
 */
public final class JavaCodeGenerator extends JavaParserBaseListener {

    /**
     * The root <tt>TypeStatement</tt> of the parsed document.
     *
     * @see TypeStatement
     */
    private TypeStatement root = null;
    /**
     * This is used to make sure {@link #enterTypeDeclaration(TypeDeclarationContext)} is only called once, since it
     * sets the {@link #root} property. Other type declarations are parsed elsewhere.
     */
    private boolean processedMainTypeDecl = false;

    private static void applyModifiers(ClassDescriptor.Builder builder, List<ClassOrInterfaceModifierContext> ctxs) {
        for (ClassOrInterfaceModifierContext mod : ctxs) {
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

    private static void applyModifiers(AnnotationDescriptor.Builder builder,
            List<ClassOrInterfaceModifierContext> ctxs) {
        for (ClassOrInterfaceModifierContext mod : ctxs) {
            if (mod.PUBLIC() != null) {
                builder.visibilityModifier(VisibilityModifier.PUBLIC);
            } else if (mod.PRIVATE() != null) {
                builder.visibilityModifier(VisibilityModifier.PRIVATE);
            } else if (mod.PROTECTED() != null) {
                builder.visibilityModifier(VisibilityModifier.PROTECTED);
            } else if (mod.ABSTRACT() != null) {
                builder.abstractModifier(true);
            } else if (mod.STRICTFP() != null) {
                builder.strictfpModifier(true);
            }
        }
    }

    private static void applyModifiers(EnumDescriptor.Builder builder, List<ClassOrInterfaceModifierContext> ctxs) {
        for (ClassOrInterfaceModifierContext mod : ctxs) {
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
            }
        }
    }

    private static void applyModifier(MethodDescriptor.Builder builder, ModifierContext mod, boolean ignoreFinal) {
        if (mod.NATIVE() != null) {
            builder.nativeModifier(true);
        }

        if (mod.SYNCHRONIZED() != null) {
            builder.synchronizedModifier(true);
        }
        ClassOrInterfaceModifierContext mods = mod.classOrInterfaceModifier();

        if (mods == null)
            return;

        if (mods.PUBLIC() != null) {
            builder.visibilityModifier(VisibilityModifier.PUBLIC);
        } else if (mods.PRIVATE() != null) {
            builder.visibilityModifier(VisibilityModifier.PRIVATE);
        } else if (mods.PROTECTED() != null) {
            builder.visibilityModifier(VisibilityModifier.PROTECTED);
        } else if (mods.ABSTRACT() != null) {
            builder.abstractModifier(true);
        } else if (mods.FINAL() != null && !ignoreFinal) {
            builder.finalModifier(true);
        } else if (mods.STATIC() != null) {
            builder.staticModifier(true);
        } else if (mods.STRICTFP() != null) {
            builder.strictfpModifier(true);
        }
    }

    private static void applyModifier(MethodDescriptor.Builder builder, InterfaceMethodModifierContext mod) {
        if (mod == null)
            return;

        if (mod.PUBLIC() != null) {
            builder.visibilityModifier(VisibilityModifier.PUBLIC);
        } else if (mod.ABSTRACT() != null) {
            builder.abstractModifier(true);
        } else if (mod.DEFAULT() != null) {
            builder.defaultModifier(true);
        } else if (mod.STATIC() != null) {
            builder.staticModifier(true);
        } else if (mod.STRICTFP() != null) {
            builder.strictfpModifier(true);
        }
    }

    /**
     * Collects only the classOrInterfaceModifier elements.
     *
     * @param mods
     * @return
     */
    private static List<ClassOrInterfaceModifierContext> parseNonTypeModifiers(List<ModifierContext> mods) {
        return mods.stream().filter(mod -> mod.classOrInterfaceModifier() != null)
                .map(ModifierContext::classOrInterfaceModifier)
                .collect(Collectors.toList());
    }

    private static List<Annotation> parseVariableModifierAnnotations(List<VariableModifierContext> ctxs) {
        List<Annotation> annotations = new ArrayList<>();

        for (VariableModifierContext ctx : ctxs) {
            if (ctx.annotation() != null) {
                annotations.add(parseAnnotation(ctx.annotation()));
            }
        }
        return annotations;
    }

    private static List<Annotation> parseInterfaceModifierAnnotations(List<InterfaceMethodModifierContext> ctxs) {
        List<Annotation> annotations = new ArrayList<>();

        for (InterfaceMethodModifierContext ctx : ctxs) {
            if (ctx.annotation() != null) {
                annotations.add(parseAnnotation(ctx.annotation()));
            }
        }
        return annotations;
    }

    private static List<Annotation> parseModifierAnnotations(List<ModifierContext> ctxs) {
        List<Annotation> annotations = new ArrayList<>();

        for (ModifierContext modifierContext : ctxs) {
            ClassOrInterfaceModifierContext ctx = modifierContext.classOrInterfaceModifier();

            if (ctx != null && ctx.annotation() != null) {
                annotations.add(parseAnnotation(ctx.annotation()));
            }
        }
        return annotations;
    }

    private static List<Annotation> parseClassOrInterfaceAnnotations(List<ClassOrInterfaceModifierContext> ctxs) {
        List<Annotation> annotations = new ArrayList<>();

        for (ClassOrInterfaceModifierContext ctx : ctxs) {
            if (ctx.annotation() != null) {
                annotations.add(parseAnnotation(ctx.annotation()));
            }
        }
        return annotations;
    }

    private static List<Annotation> parseAnnotations(List<AnnotationContext> ctxs) {
        List<Annotation> annotations = new ArrayList<>();

        for (AnnotationContext ctx : ctxs) {
            annotations.add(parseAnnotation(ctx));
        }
        return annotations;
    }

    private static Annotation parseAnnotation(AnnotationContext ctx) {
        String identifierName = ctx.qualifiedName().getText();
        Optional<Annotation.Value> value = Optional.empty();

        if (ctx.elementValuePairs() != null) {
            List<Annotation.Value> values = new ArrayList<>();

            for (ElementValuePairContext pair : ctx.elementValuePairs().elementValuePair()) {
                values.add(parseAnnotationElementValue(pair.IDENTIFIER().getText(), pair.elementValue()));
            }
            value = Optional.of(new Annotation.Values(identifierName, values));
        } else if (ctx.elementValue() != null) {
            value = Optional.of(parseAnnotationElementValue(identifierName, ctx.elementValue()));
        }
        return new Annotation(identifierName, value);
    }

    private static Annotation.Value parseAnnotationElementValue(String identifierName, ElementValueContext ctx) {
        if (ctx.expression() != null) {
            return new Annotation.ExpressionValue(identifierName, parseExpression(ctx.expression()));
        } else if (ctx.annotation() != null) {
            return new Annotation.AnnotationValue(identifierName, parseAnnotation(ctx.annotation()));
        } else {
            List<Annotation.Value> expressions = new ArrayList<>();

            for (ElementValueContext valCtx : ctx.elementValueArrayInitializer().elementValue()) {
                expressions.add(parseAnnotationElementValue(identifierName, valCtx));
            }
            return new Annotation.Values(identifierName, expressions);
        }
    }

    private static boolean containsFinal(List<VariableModifierContext> mods) {
        return mods.stream().anyMatch(vm -> vm.FINAL() != null);
    }

    private static boolean containsAbstract(List<ModifierContext> mods) {
        return mods.stream()
                .filter(vm -> vm.classOrInterfaceModifier() != null)
                .anyMatch(vm -> vm.classOrInterfaceModifier().ABSTRACT() != null);
    }

    private static List<ParameterVariableStatement> parseParameters(FormalParameterListContext ctx) {
        if (ctx == null)
            return new ArrayList<>();
        List<ParameterVariableStatement> variables = new ArrayList<>();

        for (FormalParameterContext p : ctx.formalParameter()) { // regular args
            variables.add(parseParameter(p.variableDeclaratorId(), p.typeType(), p.variableModifier(), false));
        }

        if (ctx.lastFormalParameter() != null) { // 'final' argument is varargs
            LastFormalParameterContext last = ctx.lastFormalParameter();
            variables.add(parseParameter(last.variableDeclaratorId(), last.typeType(), last.variableModifier(), true));
        }
        return variables;
    }

    private static ParameterVariableStatement parseParameter(VariableDeclaratorIdContext variableDeclaratorCtx,
            TypeTypeContext typeCtx, List<VariableModifierContext> modifiers, boolean varargs) {
        String name = variableDeclaratorCtx.IDENTIFIER().getText();
        String type = appendBracketsToType(typeCtx.getText(), variableDeclaratorCtx.LBRACK());
        boolean finalModifier = containsFinal(modifiers);
        List<Annotation> annotations = parseVariableModifierAnnotations(modifiers);
        ParameterVariableDescriptor descriptor = new ParameterVariableDescriptor.Builder()
                .identifierName(name)
                .identifierType(type + (varargs ? "..." : ""))
                .finalModifier(finalModifier)
                .build();
        return new ParameterVariableStatement(descriptor, annotations);
    }

    private static List<String> parseTypesList(TypeListContext ctx) {
        if (ctx == null)
            return new ArrayList<>();
        return ctx.typeType().stream()
                .map(TypeTypeContext::getText)
                .collect(Collectors.toList());
    }

    private static List<String> parseThrows(QualifiedNameListContext ctx) {
        if (ctx == null)
            return new ArrayList<>();
        List<String> throwsList = new ArrayList<>();

        for (QualifiedNameContext q : ctx.qualifiedName()) {
            for (TerminalNode identifier : q.IDENTIFIER()) {
                throwsList.add(identifier.getText());
            }
        }
        return throwsList;
    }

    private static List<String> parseTypeParameters(TypeParametersContext ctx) {
        if (ctx == null)
            return new ArrayList<>();
        List<String> typeParameters = new ArrayList<>();

        for (TypeParameterContext typeParam : ctx.typeParameter()) {
            String identifier = typeParam.IDENTIFIER().getText();
            String typeParamPostfix = (typeParam.typeBound() != null)
                    ? " extends " + typeParam.typeBound().getText() : "";
            typeParameters.add(identifier + typeParamPostfix);
        }
        return typeParameters;
    }

    private static void applyInstanceVariableModifiers(InstanceVariableDescriptor.Builder builder,
            ModifierContext mod) {
        // Illegal modifiers ignored: NATIVE, SYNCHRONIZED
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
            // Illegal modifiers ignored: STRICTFP, ABSTRACT
        }
    }

    private static MethodStatement parseMethod(TerminalNode identifier, TypeTypeOrVoidContext returnType,
            List<ModifierContext> modifiers, FormalParameterListContext parameterCtx,
            QualifiedNameListContext throwsCtx, boolean overridden, BlockContext blockCtx) {
        return parseMethod(identifier, returnType, modifiers, parameterCtx, throwsCtx, overridden, blockCtx, null);
    }

    private static MethodStatement parseMethod(TerminalNode identifier, TypeTypeOrVoidContext returnType,
            List<ModifierContext> modifiers, FormalParameterListContext parameterCtx,
            QualifiedNameListContext throwsCtx, boolean overridden, BlockContext blockCtx,
            TypeParametersContext typeParametersCtx) {
        MethodDescriptor.Builder builder = methodBuilder(identifier, returnType, throwsCtx, overridden);

        // Modifiers
        modifiers.forEach(mod -> applyModifier(builder, mod, false));

        // Type parameters
        builder.typeParameters(parseTypeParameters(typeParametersCtx));

        // Parameters
        List<ParameterVariableStatement> params = parseParameters(parameterCtx);

        // Body
        BlockStatement block = parseBlockStatements(blockCtx);

        // Annotations
        List<Annotation> annotations = parseModifierAnnotations(modifiers);

        MethodDescriptor descriptor = builder
                .parameterCount(params.size())
                .parameters(params.stream().map(ParameterVariableStatement::getDescriptor).collect(Collectors.toList()))
                .build();
        return new MethodStatement(descriptor, params, block, annotations);
    }

    private static MethodStatement parseInterfaceMethod(TerminalNode identifier, TypeTypeOrVoidContext returnType,
            List<ModifierContext> intBodyMods, List<InterfaceMethodModifierContext> modifiers,
            FormalParameterListContext parameterCtx, QualifiedNameListContext throwsCtx, boolean overridden,
            BlockContext blockCtx) {
        return parseInterfaceMethod(identifier, returnType, intBodyMods, modifiers, parameterCtx, throwsCtx, overridden,
                blockCtx, null);
    }

    private static MethodStatement parseInterfaceMethod(TerminalNode identifier, TypeTypeOrVoidContext returnType,
            List<ModifierContext> intBodyMods, List<InterfaceMethodModifierContext> modifiers, FormalParameterListContext parameterCtx,
            QualifiedNameListContext throwsCtx, boolean overridden, BlockContext blockCtx,
            TypeParametersContext typeParametersCtx) {
        MethodDescriptor.Builder builder = methodBuilder(identifier, returnType, throwsCtx, overridden);

        // Modifiers
        intBodyMods.forEach(mod -> applyModifier(builder, mod, true));
        modifiers.forEach(mod -> applyModifier(builder, mod));

        // Type parameters
        builder.typeParameters(parseTypeParameters(typeParametersCtx));

        // Parameters
        List<ParameterVariableStatement> params = parseParameters(parameterCtx);

        // Body
        BlockStatement block = parseBlockStatements(blockCtx);

        // Annotations
        List<Annotation> annotations = parseInterfaceModifierAnnotations(modifiers);

        MethodDescriptor descriptor = builder
                .parameterCount(params.size())
                .parameters(params.stream().map(ParameterVariableStatement::getDescriptor).collect(Collectors.toList()))
                .build();
        return new MethodStatement(descriptor, params, block, annotations);
    }

    private static MethodDescriptor.Builder methodBuilder(TerminalNode identifier, TypeTypeOrVoidContext returnType,
            QualifiedNameListContext throwsCtx, boolean overridden) {
        String identifierName = identifier.getText();
        return MethodDescriptor.Builder.allFalse(identifierName)
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .overridden(overridden)
                .returnType(returnType.getText())
                .thrownExceptions(parseThrows(throwsCtx));
    }

    private static ConstructorStatement parseConstructor(TerminalNode identifier, List<ModifierContext> modifiers,
            FormalParameterListContext parameterCtx, QualifiedNameListContext throwsCtx, BlockContext block) {
        return parseConstructor(identifier, modifiers, parameterCtx, throwsCtx, block, null);
    }

    private static ConstructorStatement parseConstructor(TerminalNode identifier, List<ModifierContext> modifiers,
            FormalParameterListContext parameterCtx, QualifiedNameListContext throwsCtx, BlockContext blockCtx,
            TypeParametersContext typeParametersCtx) {
        ConstructorStatement.Builder builder = new ConstructorStatement.Builder(identifier.getText())
                .thrownExceptions(parseThrows(throwsCtx))
                .annotations(parseModifierAnnotations(modifiers));

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

        // Body
        if (blockCtx != null) {
            BlockStatement block = parseBlockStatements(blockCtx);
            builder.block(block);
        }

        // Type parameters
        if (typeParametersCtx != null) {
            builder.typeParameters(parseTypeParameters(typeParametersCtx));
        }

        // Parameters
        List<ParameterVariableStatement> params = parseParameters(parameterCtx);
        return builder.parameterCount(params.size()).parameters(params).build();
    }

    private static Expression parseExpression(ExpressionContext ctx) {
        if (ctx.primary() != null) {
            PrimaryContext primary = ctx.primary();
            UnitExpression.ValueType valueType;

            if (primary.LPAREN() != null && primary.RPAREN() != null) {
                return new ParenthesisExpression(parseExpression(primary.expression()));
            } else if (primary.DOT() != null && primary.CLASS() != null) {
                valueType = UnitExpression.ValueType.CLASS_REFERENCE;
            } else if (primary.methodReference() != null) {
                valueType = UnitExpression.ValueType.METHOD_REFERENCE;
            } else if (primary.THIS() != null) {
                valueType = UnitExpression.ValueType.THIS;
            } else if (primary.SUPER() != null) {
                valueType = UnitExpression.ValueType.SUPER;
            } else if (primary.literal() != null) {
                valueType = UnitExpression.ValueType.LITERAL;
            } else if (primary.IDENTIFIER() != null) {
                valueType = UnitExpression.ValueType.IDENTIFIER;
            } else { // nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
                valueType = (primary.arguments() != null) ? UnitExpression.ValueType.THIS_CALL : UnitExpression.ValueType.SUPER_CALL;
            }
            return new UnitExpression(valueType, primary.getText());
        }

        // Binary operations
        if (ctx.bop != null) {
            BinaryOperation op = BinaryOperation.forSymbol(ctx.bop.getText());
            Expression left = parseExpression(ctx.expression(0));

            if (op == BinaryOperation.DOT) {
                Expression right = null;

                if (ctx.IDENTIFIER() != null) {
                    right = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, ctx.IDENTIFIER().getText());
                } else if (ctx.THIS() != null) {
                    right = new UnitExpression(UnitExpression.ValueType.THIS, ctx.THIS().getText());
                } else if (ctx.SUPER() != null) {
                    right = new UnitExpression(UnitExpression.ValueType.SUPER_CALL, ctx.superSuffix().getText());
                } else if (ctx.NEW() != null) {
                    String typeArg = (ctx.nonWildcardTypeArguments() == null) ? ""
                            : ctx.nonWildcardTypeArguments().getText();
                    right = new UnitExpression(UnitExpression.ValueType.NEW, typeArg + ctx.innerCreator().getText());
                } else if (ctx.explicitGenericInvocation() != null) {
                    ExplicitGenericInvocationContext invokCtx = ctx.explicitGenericInvocation();
                    UnitExpression.ValueType valueType = (invokCtx.explicitGenericInvocationSuffix().SUPER() == null)
                            ? UnitExpression.ValueType.METHOD_CALL : UnitExpression.ValueType.SUPER_CALL;
                    right = new UnitExpression(valueType, invokCtx.getText());
                }
                return new BinaryExpression(left, op, right);
            } else if (op == BinaryOperation.QUESTION) {
                Expression valueIfTrue = parseExpression(ctx.expression(1));
                Expression valueIfFalse = parseExpression(ctx.expression(2));
                return new TernaryExpression(left, valueIfTrue, valueIfFalse);
            } else if (op == BinaryOperation.INSTANCE_OF) {
                Expression unitExpression = new UnitExpression(UnitExpression.ValueType.TYPE,
                        ctx.typeType().getText());
                return new BinaryExpression(left, op, unitExpression);
            }
            Expression right = parseExpression(ctx.expression(1));
            return new BinaryExpression(left, op, right);
        }

        // Lambda expression
        if (ctx.lambdaExpression() != null) {
            LambdaParametersContext params = ctx.lambdaExpression().lambdaParameters();
            LambdaBodyContext body = ctx.lambdaExpression().lambdaBody();

            // Parameters
            LambdaParameter parameter;

            if (params.LPAREN() == null && params.IDENTIFIER().size() == 1) {
                parameter = new LambdaParameter.Identifier(params.IDENTIFIER(0).getText());
            } else if (params.LPAREN() != null && params.IDENTIFIER().size() > 0) {
                List<String> identifiers = new ArrayList<>();

                for (TerminalNode ident : params.IDENTIFIER()) {
                    identifiers.add(ident.getText());
                }
                parameter = new LambdaParameter.Identifiers(identifiers);
            } else {
                parameter = new LambdaParameter.ParameterVariables(parseParameters(params.formalParameterList()));
            }

            // Body
            if (body.expression() != null) {
                return new LambdaExpression(parameter, new ExpressionStatement(parseExpression(body.expression())));
            } else {
                return new LambdaExpression(parameter, parseBlockStatements(body.block()));
            }
        }

        // Define class or array
        if (ctx.NEW() != null) {
            CreatorContext creator = ctx.creator();
            String identifierName = creator.createdName().getText();

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
                ClassDescriptor descriptor = ClassDescriptor.Builder.allFalse(identifierName)
                        .local(true)
                        .build();
                BlockStatement statement = rest.classBody() != null
                        ? parseClassBodyDeclaration(rest.classBody().classBodyDeclaration())
                        : BlockStatement.EMPTY;
                List<Annotation> annotations = new ArrayList<>();
                return new InstantiateClassExpression(new ClassStatement(descriptor, statement, annotations), arguments,
                        typeArgs, hasTypeArguments);
            } else { // createdName arrayCreatorRest
                ArrayCreatorRestContext rest = creator.arrayCreatorRest();
                List<Expression> contents = new ArrayList<>();

                if (rest.arrayInitializer() != null) { // squareBrackets squareBrackets* arrayInitializer
                    for (SquareBracketsContext sq : rest.squareBrackets()) {
                        contents.add(new SquareBracketsExpression(Optional.empty()));
                    }
                    List<Expression> expressions = new ArrayList<>();

                    for (VariableInitializerContext initCtx : rest.arrayInitializer().variableInitializer()) {
                        expressions.add(parseVariableInitializerExpression(initCtx));
                    }
                    contents.add(new ArrayExpression(expressions));
                } else { // squareBracketsExpression squareBracketsExpression* squareBrackets*
                    for (SquareBracketsExpressionContext sqExprCtx : rest.squareBracketsExpression()) {
                        Optional<Expression> expr = Optional.of(parseExpression(sqExprCtx.expression()));
                        contents.add(new SquareBracketsExpression(expr));
                    }

                    for (SquareBracketsContext sq : rest.squareBrackets()) {
                        contents.add(new SquareBracketsExpression(Optional.empty()));
                    }
                }
                return new ArrayDefinitionExpression(contents);
            }
        }

        // Cast
        if (ctx.LPAREN() != null && ctx.RPAREN() != null && ctx.typeType() != null & ctx.expression().size() == 1) {
            return new CastExpression(ctx.typeType().getText(), parseExpression(ctx.expression(0)));
        }

        // Array access
        if (ctx.LBRACK() != null && ctx.RBRACK() != null) {
            Expression array = parseExpression(ctx.expression(0));
            Expression index = parseExpression(ctx.expression(1));
            return new ArrayAccessExpression(array, index);
        }

        // Prefix
        if (ctx.prefix != null) {
            Prefix prefix = Prefix.forSymbol(ctx.prefix.getText());
            Expression expr = parseExpression(ctx.expression(0));
            return new PrefixedExpression(expr, prefix);
        }

        // Postfix
        if (ctx.postfix != null) {
            Postfix postfix = Postfix.forSymbol(ctx.postfix.getText());
            Expression expr = parseExpression(ctx.expression(0));
            return new PostfixedExpression(expr, postfix);
        }

        // Fall-back: expression LPAREN expressionList? RPAREN
        if (ctx.expression().size() > 0) {
            Expression method = parseExpression(ctx.expression(0));
            List<Expression> parameters = new ArrayList<>();

            if (ctx.expressionList() != null) {
                for (ExpressionContext ectx : ctx.expressionList().expression()) {
                    parameters.add(parseExpression(ectx));
                }
            }
            return new MethodCallExpression(method, parameters);
        }
        throw new IllegalArgumentException("invalid context");
    }

    private static BlockStatement parseBlockStatements(BlockContext ctx) {
        return ctx == null ? BlockStatement.EMPTY : parseBlockStatements(ctx.blockStatement());
    }

    private static BlockStatement parseBlockStatements(List<BlockStatementContext> ctxs) {
        return ctxs == null ? BlockStatement.EMPTY
                : new BlockStatement(ctxs.stream().map(JavaCodeGenerator::parseBlockStatement)
                        .collect(Collectors.toList()));
    }

    private static Statement parseBlockStatement(BlockStatementContext st) {
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
        LocalTypeDeclarationContext localDec = st.localTypeDeclaration();

        // XXX sometimes innerDec is null, although the grammar implies it should never be
        if (localDec == null || localDec.SEMI() != null) { // semi-colon
            return new SemiColonStatement();
        } else if (localDec.classDeclaration() != null) {
            return parseClass(localDec.classOrInterfaceModifier(), localDec.classDeclaration(), true, false);
        } else {
            return parseInterface(localDec.classOrInterfaceModifier(), localDec.interfaceDeclaration(), true, false);
        }
    }

    private static SwitchStatement.SwitchLabelStatement parseSwitchLabel(SwitchLabelContext ctx) {
        if (ctx.constantExpression != null) {
            return new SwitchStatement.SwitchLabelStatement(parseExpression(ctx.constantExpression));
        } else if (ctx.enumConstantName != null) {
            return new SwitchStatement.SwitchLabelStatement(ctx.enumConstantName.getText());
        } else {
            return new SwitchStatement.SwitchLabelStatement("default");
        }
    }

    private static LocalVariableStatements parseLocalVariables(LocalVariableDeclarationContext dec) {
        List<LocalVariableStatement> locals = new ArrayList<>();
        List<Annotation> annotations = parseVariableModifierAnnotations(dec.variableModifier());
        boolean finalModifier = containsFinal(dec.variableModifier());
        final String identifierType = dec.typeType().getText();

        for (VariableDeclaratorContext decl : dec.variableDeclarators().variableDeclarator()) {
            VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
            String identifierName = identifierCtx.IDENTIFIER().getText();
            String type = appendBracketsToType(identifierType, identifierCtx.LBRACK());
            LocalVariableDescriptor desc = new LocalVariableDescriptor.Builder(identifierName)
                    .identifierType(type)
                    .finalModifier(finalModifier)
                    .build();
            Optional<Expression> value = Optional.empty();

            if (decl.variableInitializer() != null) {
                value = Optional.of(parseVariableInitializerExpression(decl.variableInitializer()));
            }
            locals.add(new LocalVariableStatement(desc, value, annotations));
        }
        return new LocalVariableStatements(locals);
    }

    private static Statement parseStatement(StatementContext ctx) {
        if (ctx.THROW() != null) {
            return new ThrowStatement(parseExpression(ctx.expression(0)));
        } else if (ctx.SWITCH() != null) {
            Expression condition = new ParenthesisExpression(parseExpression(ctx.parExpression().expression()));
            List<Statement> elements = new ArrayList<>();

            for (SwitchBlockStatementGroupContext group : ctx.switchBlockStatementGroup()) {
                for (BlockStatementContext block : group.blockStatement()) {
                    elements.add(parseBlockStatement(block));
                }

                for (SwitchLabelContext label : group.switchLabel()) {
                    elements.add(parseSwitchLabel(label));
                }
            }

            for (SwitchLabelContext label : ctx.switchLabel()) {
                elements.add(parseSwitchLabel(label));
            }
            return new SwitchStatement(condition, new BlockStatement(elements));
        } else if (ctx.FOR() != null) {
            Statement statement = parseStatement(ctx.statement(0));

            if (ctx.forControl().enhancedForControl() != null) { // for-each
                EnhancedForControlContext en = ctx.forControl().enhancedForControl();

                // Variable
                boolean finalModifier = containsFinal(en.variableModifier());
                List<Annotation> annotations = parseVariableModifierAnnotations(en.variableModifier());

                VariableDeclaratorIdContext identifierCtx = en.variableDeclaratorId();
                String identifierName = identifierCtx.IDENTIFIER().getText();
                String identifierType = appendBracketsToType(en.typeType().getText(), identifierCtx.LBRACK());
                LocalVariableDescriptor desc = new LocalVariableDescriptor.Builder(identifierName)
                        .finalModifier(finalModifier)
                        .identifierType(identifierType)
                        .build();
                LocalVariableStatement local = new LocalVariableStatement(desc, Optional.empty(), annotations);
                Expression collection = parseExpression(en.expression());
                return new ForEachStatement(local, collection, statement);
            } else { // for-loop
                ForControlContext forCtx = ctx.forControl();

                // For init
                ForInitContext init = forCtx.forInit();
                Optional<LocalVariableStatements> initVariables = Optional.empty();
                List<Expression> initExpressions = new ArrayList<>();

                if (init != null) {
                    if (init.localVariableDeclaration() != null) {
                        initVariables = Optional.of(parseLocalVariables(init.localVariableDeclaration()));
                    } else {
                        for (ExpressionContext ectx : init.expressionList().expression()) {
                            initExpressions.add(parseExpression(ectx));
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
                        updateExpressions.add(parseExpression(ectx));
                    }
                }
                return new ForStatement(initVariables, initExpressions, condition, updateExpressions);
            }
        } else if (ctx.IF() != null) {
            Expression condition = new ParenthesisExpression(parseExpression(ctx.parExpression().expression()));
            Statement statement = parseStatement(ctx.statement(0));
            Optional<Statement> elseStatement = (ctx.statement().size() == 2)
                    ? Optional.of(parseStatement(ctx.statement(1))) : Optional.empty();
            return new IfStatement(condition, statement, elseStatement);
        } else if (ctx.TRY() != null) {
            if (ctx.resourceSpecification() != null) { // try w/ resources
                // Try
                List<LocalVariableStatement> resources = new ArrayList<>();

                for (ResourceContext res : ctx.resourceSpecification().resources().resource()) {
                    boolean finalModifier = containsFinal(res.variableModifier());
                    List<Annotation> annotations = parseVariableModifierAnnotations(res.variableModifier());
                    VariableDeclaratorIdContext identifierCtx = res.variableDeclaratorId();
                    String identifierType = appendBracketsToType(res.classOrInterfaceType().getText(),
                            identifierCtx.LBRACK());
                    String identifierName = identifierCtx.IDENTIFIER().getText();
                    Expression value = parseExpression(res.expression());
                    LocalVariableDescriptor desc = new LocalVariableDescriptor.Builder(identifierName)
                            .identifierType(identifierType)
                            .finalModifier(finalModifier)
                            .build();
                    resources.add(new LocalVariableStatement(desc, Optional.of(value), annotations));
                }
                BlockStatement block = parseBlockStatements(ctx.block());

                // Catch
                List<CatchStatement> catchStatements = parseCatchStatements(ctx.catchClause());

                // Finally
                BlockStatement finallyBlock = parseFinallyBlock(ctx.finallyBlock());
                return new TryWithResourcesStatement(block, catchStatements, finallyBlock, resources);
            } else { // try-catch
                // Try
                BlockStatement block = parseBlockStatements(ctx.block());

                // Catch
                List<CatchStatement> catchStatements = parseCatchStatements(ctx.catchClause());

                // Finally
                BlockStatement finallyBlock = parseFinallyBlock(ctx.finallyBlock());
                return new TryStatement(block, catchStatements, finallyBlock);
            }
        } else if (ctx.RETURN() != null) {
            Optional<Expression> expr = (ctx.expression().size() == 1)
                    ? Optional.of(parseExpression(ctx.expression(0))) : Optional.empty();
            return new ReturnStatement(expr);
        } else if (ctx.statementExpression != null) { // statementExpression=expression SEMI
            Expression expr = parseExpression(ctx.statementExpression);
            return new ExpressionStatement(expr);
        } else if (ctx.BREAK() != null) {
            Optional<String> identifier = getTextOrEmpty(ctx.IDENTIFIER());
            return new BreakStatement(identifier);
        } else if (ctx.CONTINUE() != null) {
            Optional<String> identifier = getTextOrEmpty(ctx.IDENTIFIER());
            return new ContinueStatement(identifier);
        } else if (ctx.ASSERT() != null) {
            Expression expr = parseExpression(ctx.expression(0));
            Optional<Expression> errorMsg = (ctx.expression().size() == 2)
                    ? Optional.of(parseExpression(ctx.expression(1))) : Optional.empty();
            return new AssertStatement(expr, errorMsg);
        } else if (ctx.DO() != null) {
            Expression condition = new ParenthesisExpression(parseExpression(ctx.parExpression().expression()));
            Statement statement = parseStatement(ctx.statement(0));
            return new DoWhileStatement(condition, statement);
        } else if (ctx.SYNCHRONIZED() != null) {
            Expression target = new ParenthesisExpression(parseExpression(ctx.parExpression().expression()));
            BlockStatement block = parseBlockStatements(ctx.block());
            return new SynchronizedStatement(target, block);
        } else if (ctx.WHILE() != null && ctx.DO() == null) {
            Expression condition = new ParenthesisExpression(parseExpression(ctx.parExpression().expression()));
            Statement statement = parseStatement(ctx.statement(0));
            return new WhileStatement(condition, statement);
        } else if (ctx.blockLabel != null) {
            return parseBlockStatements(ctx.blockLabel.blockStatement());
        } else if (ctx.identifierLabel != null) {
            Statement statement = parseStatement(ctx.statement(0));
            return new LabelStatement(ctx.IDENTIFIER().getText(), statement);
        } else { // semi colon
            return new SemiColonStatement();
        }
    }

    private static BlockStatement parseFinallyBlock(FinallyBlockContext ctx) {
        return ctx != null ? parseBlockStatements(ctx.block()) : BlockStatement.EMPTY;
    }

    private static List<CatchStatement> parseCatchStatements(List<CatchClauseContext> ctxs) {
        List<CatchStatement> catchStatements = new ArrayList<>();

        for (CatchClauseContext catchClauseCtx : ctxs) {
            boolean finalModifier = containsFinal(catchClauseCtx.variableModifier());
            List<Annotation> annotations = parseVariableModifierAnnotations(catchClauseCtx.variableModifier());
            String identifierName = catchClauseCtx.IDENTIFIER().getText();
            List<LocalVariableStatement> locals = new ArrayList<>();

            for (QualifiedNameContext qn : catchClauseCtx.catchType().qualifiedName()) {
                String identifierType = qn.getText();
                LocalVariableDescriptor desc = new LocalVariableDescriptor.Builder(identifierName)
                        .identifierType(identifierType)
                        .finalModifier(finalModifier)
                        .build();
                locals.add(new LocalVariableStatement(desc, Optional.empty(), annotations));
            }
            BlockStatement catchBlock = parseBlockStatements(catchClauseCtx.block());
            catchStatements.add(new CatchStatement(new LocalVariableStatements(locals), catchBlock));
        }
        return catchStatements;
    }

    private static Expression parseVariableInitializerExpression(VariableInitializerContext ctx) {
        if (ctx.expression() != null) {
            return parseExpression(ctx.expression());
        } else { // array initializer
            List<Expression> expressions = new ArrayList<>();

            for (VariableInitializerContext initCtx : ctx.arrayInitializer().variableInitializer()) {
                expressions.add(parseVariableInitializerExpression(initCtx));
            }
            return new ArrayExpression(expressions);
        }
    }

    private static AnnotationStatement parseAnnotationDefinition(List<ClassOrInterfaceModifierContext> ctxs,
            AnnotationTypeDeclarationContext decCtx) {
        return parseAnnotationDefinition(ctxs, decCtx, false);
    }

    private static AnnotationStatement parseAnnotationDefinition(List<ClassOrInterfaceModifierContext> modifiers,
            AnnotationTypeDeclarationContext dec, boolean inner) {
        String identifierName = dec.IDENTIFIER().getText();
        AnnotationDescriptor.Builder builder = new AnnotationDescriptor.Builder(identifierName)
                .inner(inner)
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);

        // Modifiers
        applyModifiers(builder, modifiers);

        // Body
        BlockStatement block = parseAnnotationBody(dec.annotationTypeBody().annotationTypeElementDeclaration());

        // Annotations
        List<Annotation> annotations = parseClassOrInterfaceAnnotations(modifiers);
        return new AnnotationStatement(builder.build(), block, annotations);
    }

    private static BlockStatement parseAnnotationBody(List<AnnotationTypeElementDeclarationContext> ctxs) {
        if (ctxs == null)
            return BlockStatement.EMPTY;
        List<Statement> statements = new ArrayList<>();

        for (AnnotationTypeElementDeclarationContext ctx : ctxs) {
            AnnotationTypeElementRestContext typeDecl = ctx.annotationTypeElementRest();
            AnnotationMethodOrConstantRestContext multiDec = typeDecl.annotationMethodOrConstantRest();
            ClassDeclarationContext clazzDec = typeDecl.classDeclaration();
            InterfaceDeclarationContext intDec = typeDecl.interfaceDeclaration();
            EnumDeclarationContext enumDec = typeDecl.enumDeclaration();
            AnnotationTypeDeclarationContext anonDec = typeDecl.annotationTypeDeclaration();

            if (clazzDec != null) {
                statements.add(parseClass(parseNonTypeModifiers(ctx.modifier()), clazzDec, false, true));
            } else if (intDec != null) {
                statements.add(parseInterface(parseNonTypeModifiers(ctx.modifier()), intDec, false, true));
            } else if (enumDec != null) {
                statements.add(parseEnum(parseNonTypeModifiers(ctx.modifier()), enumDec, true));
            } else if (anonDec != null) {
                statements.add(parseAnnotationDefinition(parseNonTypeModifiers(ctx.modifier()), anonDec));
            } else { // multiDec
                // TODO test this and make sure it works
                AnnotationMethodRestContext methodRest = multiDec.annotationMethodRest();
                List<Annotation> annotations = parseModifierAnnotations(ctx.modifier());
                final String identifierType = typeDecl.typeType().getText();

                if (methodRest != null) { // annotationMethodRest
                    VisibilityModifier visibilityModifier = VisibilityModifier.PACKAGE_PRIVATE;

                    for (ModifierContext mc : ctx.modifier()) {
                        if (mc.classOrInterfaceModifier() != null && mc.classOrInterfaceModifier().PUBLIC() != null) {
                            visibilityModifier = VisibilityModifier.PUBLIC;
                            break;
                        }
                    }
                    boolean abstractModifier = containsAbstract(ctx.modifier());
                    String variableIdentifierName = methodRest.IDENTIFIER().getText();
                    Optional<Annotation.Value> defaultValue = Optional.empty();
                    DefaultValueContext defaultCtx = methodRest.defaultValue();

                    if (defaultCtx != null) {
                        defaultValue = Optional.of(parseAnnotationElementValue(variableIdentifierName,
                                defaultCtx.elementValue()));
                    }
                    statements.add(new AnnotationStatement.AnnotationMethod(visibilityModifier, abstractModifier,
                            variableIdentifierName, defaultValue, annotations));
                } else { // annotationConstantRest
                    VariableDeclaratorsContext decs = multiDec.annotationConstantRest().variableDeclarators();

                    for (VariableDeclaratorContext decl : decs.variableDeclarator()) {
                        VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                        String identifierName = identifierCtx.IDENTIFIER().getText();
                        String type = appendBracketsToType(identifierType, identifierCtx.LBRACK());
                        InstanceVariableDescriptor.Builder builder
                                = InstanceVariableDescriptor.Builder.allFalse(identifierName)
                                .identifierType(type)
                                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
                        ctx.modifier().forEach(mod -> applyInstanceVariableModifiers(builder, mod));
                        Optional<Expression> valueExpression = Optional.empty();

                        if (decl.variableInitializer() != null) {
                            valueExpression = Optional.of(parseVariableInitializerExpression(
                                    decl.variableInitializer()));
                        }
                        statements.add(new InstanceVariableStatement(builder.build(), annotations, valueExpression));
                    }
                }
            }
        }
        return new BlockStatement(statements);
    }

    private static Optional<String> getTextOrEmpty(TerminalNode node) {
        return node != null ? Optional.of(node.getText()) : Optional.empty();
    }

    private static EnumStatement parseEnum(TypeDeclarationContext ctx) {
        return parseEnum(ctx.classOrInterfaceModifier(), ctx.enumDeclaration(), false);
    }

    private static EnumStatement parseEnum(List<ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            EnumDeclarationContext dec, boolean inner) {
        String identifierName = dec.IDENTIFIER().getText();
        EnumDescriptor.Builder builder = EnumDescriptor.Builder.allFalse(identifierName)
                .inner(inner)
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);

        // Class modifiers
        applyModifiers(builder, classOrInterfaceModifierContexts);

        // Implemented interfaces
        builder.superClasses(parseTypesList(dec.typeList()));

        // Enum constants
        List<Statement> block = new ArrayList<>();

        if (dec.enumConstants() != null) {
            block.addAll(parseEnumConstantDeclaration(dec.enumConstants().enumConstant()));
        }

        // Enum body declarations
        if (dec.enumBodyDeclarations() != null && dec.enumBodyDeclarations().classBodyDeclaration() != null) {
            block.addAll(parseClassBodyDeclaration(dec.enumBodyDeclarations().classBodyDeclaration()).getStatements());
        }

        // Annotations
        List<Annotation> annotations = parseClassOrInterfaceAnnotations(classOrInterfaceModifierContexts);
        return new EnumStatement(builder.build(), new BlockStatement(block), annotations);
    }

    private static List<Statement> parseEnumConstantDeclaration(List<EnumConstantContext> ctxs) {
        if (ctxs == null)
            return new ArrayList<>();
        List<Statement> statements = new ArrayList<>();

        for (EnumConstantContext constantCtx : ctxs) {
            List<Annotation> annotations = parseAnnotations(constantCtx.annotation());
            String identifierName = constantCtx.IDENTIFIER().getText();

            // Arguments
            List<Expression> arguments = new ArrayList<>();
            ArgumentsContext args = constantCtx.arguments();

            if (args != null && args.expressionList() != null) {
                for (ExpressionContext exprCtx : args.expressionList().expression()) {
                    arguments.add(parseExpression(exprCtx));
                }
            }

            // Body
            BlockStatement body = constantCtx.classBody() == null ? BlockStatement.EMPTY
                    : parseClassBodyDeclaration(constantCtx.classBody().classBodyDeclaration());

            if (constantCtx.classBody() != null) {
                parseClassBodyDeclaration(constantCtx.classBody().classBodyDeclaration());
            }
            EnumConstantStatement constant = new EnumConstantStatement(identifierName, arguments, body, annotations);
            statements.add(constant);
        }
        return statements;
    }

    private static ClassStatement parseClass(TypeDeclarationContext ctx) {
        return parseClass(ctx.classOrInterfaceModifier(), ctx.classDeclaration(), false, false);
    }

    private static ClassStatement parseClass(List<ClassOrInterfaceModifierContext> classOrInterfaceModifierContexts,
            ClassDeclarationContext dec, boolean local, boolean inner) {
        String identifierName = dec.IDENTIFIER().getText();
        ClassDescriptor.Builder builder = ClassDescriptor.Builder.allFalse(identifierName)
                .local(local)
                .inner(inner)
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);

        // Class modifiers
        applyModifiers(builder, classOrInterfaceModifierContexts);

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Implemented interfaces and extended class
        List<String> superClasses = parseTypesList(dec.typeList());

        TypeTypeContext extendedClass = dec.typeType();

        if (extendedClass != null) {
            superClasses.add(extendedClass.getText());
        }
        builder.superClasses(superClasses);

        // Body
        BlockStatement block = parseClassBodyDeclaration(dec.classBody().classBodyDeclaration());

        // Annotations
        List<Annotation> annotations = parseClassOrInterfaceAnnotations(classOrInterfaceModifierContexts);
        return new ClassStatement(builder.build(), block, annotations);
    }

    private static BlockStatement parseClassBodyDeclaration(List<ClassBodyDeclarationContext> ctxs) {
        if (ctxs == null)
            return BlockStatement.EMPTY;
        List<Statement> statements = new ArrayList<>();

        for (ClassBodyDeclarationContext classBody : ctxs) {
            // Static block
            if (classBody.STATIC() != null) {
                BlockStatement block = parseBlockStatements(classBody.block());
                statements.add(new StaticBlockStatement(block));
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
                MethodStatement methodStatement = parseMethod(method.IDENTIFIER(), method.typeTypeOrVoid(),
                        classBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false, method.methodBody().block());
                statements.add(methodStatement);
            } else if (genericMethod != null) { // generic method
                method = genericMethod.methodDeclaration();
                MethodStatement methodStatement = parseMethod(method.IDENTIFIER(), method.typeTypeOrVoid(),
                        classBody.modifier(), method.formalParameters().formalParameterList(),
                        method.qualifiedNameList(), false, method.methodBody().block(), genericMethod.typeParameters());
                statements.add(methodStatement);
            } else if (field != null) { // field
                final String identifierType = field.typeType().getText();

                for (VariableDeclaratorContext decl : field.variableDeclarators().variableDeclarator()) {
                    VariableDeclaratorIdContext identifierCtx = decl.variableDeclaratorId();
                    String identifierName = identifierCtx.IDENTIFIER().getText();
                    String type = appendBracketsToType(identifierType, identifierCtx.LBRACK());
                    InstanceVariableDescriptor.Builder desc
                            = InstanceVariableDescriptor.Builder.allFalse(identifierName)
                            .identifierType(type)
                            .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
                    classBody.modifier().forEach(mod -> applyInstanceVariableModifiers(desc, mod));
                    List<Annotation> annotations = parseModifierAnnotations(classBody.modifier());
                    Optional<Expression> valueExpression = Optional.empty();

                    if (decl.variableInitializer() != null) {
                        valueExpression = Optional.of(parseVariableInitializerExpression(decl.variableInitializer()));
                    }
                    statements.add(new InstanceVariableStatement(desc.build(), annotations, valueExpression));
                }
            } else if (constructor != null) { // constructor
                ConstructorStatement cons = parseConstructor(constructor.IDENTIFIER(), classBody.modifier(),
                        constructor.formalParameters().formalParameterList(), constructor.qualifiedNameList(),
                        constructor.block());
                statements.add(cons);
            } else if (genericConstructor != null) { // generic constructor
                constructor = genericConstructor.constructorDeclaration();
                ConstructorStatement genericCons = parseConstructor(constructor.IDENTIFIER(), classBody.modifier(),
                        constructor.formalParameters().formalParameterList(), constructor.qualifiedNameList(),
                        constructor.block(), genericConstructor.typeParameters());
                statements.add(genericCons);
            } else if (innerClass != null) { // inner class
                statements.add(parseClass(parseNonTypeModifiers(classBody.modifier()), innerClass, false, true));
            } else if (innerInterface != null) { // inner interface
                statements.add(
                        parseInterface(parseNonTypeModifiers(classBody.modifier()), innerInterface, false, true));
            } else if (innerEnum != null) { // inner enum
                statements.add(parseEnum(parseNonTypeModifiers(classBody.modifier()), innerEnum, true));
            }
        }
        return new BlockStatement(statements);
    }

    private static ClassStatement parseInterface(TypeDeclarationContext ctx) {
        return parseInterface(ctx.classOrInterfaceModifier(), ctx.interfaceDeclaration(), false, false);
    }

    private static ClassStatement parseInterface(List<ClassOrInterfaceModifierContext> modCtxs,
            InterfaceDeclarationContext dec, boolean local, boolean inner) {
        String identifierName = dec.IDENTIFIER().getText();
        ClassDescriptor.Builder builder = ClassDescriptor.Builder.allFalse(identifierName)
                .interfaceModifier(true)
                .local(local)
                .inner(inner)
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);

        // Class modifiers
        applyModifiers(builder, modCtxs);

        // Type parameters
        builder.typeParameters(parseTypeParameters(dec.typeParameters()));

        // Extended classes
        builder.superClasses(parseTypesList(dec.typeList()));

        // Body
        BlockStatement block = parseInterfaceBodyDeclaration(dec.interfaceBody().interfaceBodyDeclaration());

        // Annotations
        List<Annotation> annotations = parseClassOrInterfaceAnnotations(modCtxs);
        return new ClassStatement(builder.build(), block, annotations);
    }

    private static BlockStatement parseInterfaceBodyDeclaration(List<InterfaceBodyDeclarationContext> ctxs) {
        if (ctxs == null)
            return BlockStatement.EMPTY;
        List<Statement> statements = new ArrayList<>();

        for (InterfaceBodyDeclarationContext intBody : ctxs) {
            InterfaceMemberDeclarationContext memberDec = intBody.interfaceMemberDeclaration();
            InterfaceMethodDeclarationContext method = memberDec.interfaceMethodDeclaration();
            GenericInterfaceMethodDeclarationContext genericMethod
                    = memberDec.genericInterfaceMethodDeclaration();
            ClassDeclarationContext classDec = memberDec.classDeclaration();
            ConstDeclarationContext constDecl = memberDec.constDeclaration();
            EnumDeclarationContext enumDec = memberDec.enumDeclaration();
            AnnotationTypeDeclarationContext anonDecl = memberDec.annotationTypeDeclaration();

            if (method != null) { // method
                MethodStatement methodStatement = parseInterfaceMethod(method.IDENTIFIER(), method.typeTypeOrVoid(),
                        intBody.modifier(), method.interfaceMethodModifier(),
                        method.formalParameters().formalParameterList(), method.qualifiedNameList(), false,
                        method.methodBody().block());
                statements.add(methodStatement);
            } else if (genericMethod != null) { // generic method
                method = genericMethod.interfaceMethodDeclaration();
                MethodStatement methodStatement = parseInterfaceMethod(method.IDENTIFIER(), method.typeTypeOrVoid(),
                        intBody.modifier(), method.interfaceMethodModifier(),
                        method.formalParameters().formalParameterList(), method.qualifiedNameList(), false,
                        method.methodBody().block(), genericMethod.typeParameters());
                statements.add(methodStatement);
            } else if (constDecl != null) { // constant
                final String identifierType = constDecl.typeType().getText();

                for (ConstantDeclaratorContext decl : constDecl.constantDeclarator()) {
                    String identifierName = decl.IDENTIFIER().getText();
                    String type = appendBracketsToType(identifierType, decl.LBRACK());
                    InstanceVariableDescriptor.Builder desc
                            = InstanceVariableDescriptor.Builder.allFalse(identifierName)
                            .identifierType(type)
                            .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
                    intBody.modifier().forEach(mod -> applyInstanceVariableModifiers(desc, mod));
                    List<Annotation> annotations = parseModifierAnnotations(intBody.modifier());
                    Optional<Expression> valueExpression = Optional.empty();

                    if (decl.variableInitializer() != null) {
                        valueExpression = Optional.of(parseVariableInitializerExpression(decl.variableInitializer()));
                    }
                    statements.add(new InstanceVariableStatement(desc.build(), annotations, valueExpression));
                }
            } else if (classDec != null) { // inner class
                statements.add(parseClass(parseNonTypeModifiers(intBody.modifier()), classDec, false, true));
            } else if (enumDec != null) { // inner enum
                statements.add(parseEnum(parseNonTypeModifiers(intBody.modifier()), enumDec, true));
            } else if (anonDecl != null) { // inner annotation def
                statements.add(parseAnnotationDefinition(parseNonTypeModifiers(intBody.modifier()), anonDecl, true));
            }
        }
        return new BlockStatement(statements);
    }

    private static String appendBracketsToType(String identifierType, List<TerminalNode> bracketsToAppend) {
        final StringBuilder builder = new StringBuilder(identifierType);
        bracketsToAppend.forEach(b -> builder.append("[]"));
        return builder.toString();
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
        // Check if the primary type in this class was processed already
        if (processedMainTypeDecl)
            return;
        else
            processedMainTypeDecl = true;

        // Generate node and add node
        if (ctx.classDeclaration() != null) {
            root = parseClass(ctx);
        } else if (ctx.interfaceDeclaration() != null) {
            root = parseInterface(ctx);
        } else if (ctx.enumDeclaration() != null) {
            root = parseEnum(ctx);
        } else {
            root = parseAnnotationDefinition(ctx.classOrInterfaceModifier(), ctx.annotationTypeDeclaration());
        }
    }

    public TypeStatement getRootStatement() {
        return root;
    }
}
