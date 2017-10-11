package org.qmul.csar.query;

import grammars.csar.CsarParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.descriptor.*;
import org.qmul.csar.lang.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grammars.csar.CsarParser.*;

/**
 * A {@link CsarQuery} generator.
 */
class CsarQueryGenerator extends CsarParserBaseListener {

    private TargetDescriptor searchTarget;
    private final List<String> fromTarget = new ArrayList<>();
    private Optional<ContainsQuery> containsQuery = Optional.empty();
    private Optional<RefactorDescriptor> refactorDescriptor = Optional.empty();
    private final List<ContainsQueryElement> containsQueryElements = new ArrayList<>();

    private static SearchType parseSearchType(TerminalNode defNode, TerminalNode useNode) {
        if (defNode != null && useNode == null) {
            return SearchType.DEF;
        } else if (defNode == null && useNode != null) {
            return SearchType.USE;
        }
        throw new RuntimeException("invalid defNode and useNode");
    }

    /**
     * @return {@code Optional.of(ctx.getText())} if ctx is not <tt>null</tt>, and <code>Optional.empty()} otherwise.
     */
    private static Optional<String> parseTextOrEmpty(ParserRuleContext ctx) {
        return ctx != null ? Optional.of(ctx.getText()) : Optional.empty();
    }

    /**
     * @return {@code Optional.of(true)} if node is not <tt>null</tt>, and {@code Optional.empty()} otherwise.
     */
    private static Optional<Boolean> parseOptionalTrueOrEmpty(TerminalNode node) {
        return node != null ? Optional.of(true) : Optional.empty();
    }

    private static TargetDescriptor parseTargetDescriptor(StatementDescriptorContext ctx) {
        if (ctx.clazz() != null) {
            return parseClazz(ctx.clazz());
        } else if (ctx.method() != null) {
            return parseMethod(ctx.method());
        } else if (ctx.variable() != null) {
            return parseVariable(ctx.variable());
        } else if (ctx.controlFlow() != null) {
            return parseConditional(ctx.controlFlow());
        } else if (ctx.statement() != null) {
            return parseStatement(ctx.statement());
        } else {
            return parseComment(ctx.comment());
        }
    }

    private static TargetDescriptor parseClazz(ClazzContext ctx) {
        // LanguageElementHeader
        CommonModifiersContext commonModsCtx = ctx.commonModifiers();
        SearchType searchType = parseSearchType(commonModsCtx.DEF(), commonModsCtx.USE());
        Optional<VisibilityModifier> visibilityModifier = commonModsCtx.visibilityModifier() != null
                ? visibilityModifier(commonModsCtx.visibilityModifier()) : Optional.empty();

        // Other modifiers
        Optional<Boolean> staticModifier = parseOptionalTrueOrEmpty(commonModsCtx.STATIC());
        Optional<Boolean> finalModifier = parseOptionalTrueOrEmpty(commonModsCtx.FINAL());

        // classModifiers
        ClassModifiersContext classModsCtx = ctx.classModifiers();
        Optional<Boolean> abstractModifier = parseOptionalTrueOrEmpty(classModsCtx.ABSTRACT());
        Optional<Boolean> interfaceModifier = parseOptionalTrueOrEmpty(classModsCtx.INTERFACE());
        Optional<Boolean> strictfpModifier = parseOptionalTrueOrEmpty(classModsCtx.STRICTFP());
        Optional<Boolean> anonymousModifier = parseOptionalTrueOrEmpty(classModsCtx.ANONYMOUS());
        Optional<Boolean> innerModifier = parseOptionalTrueOrEmpty(classModsCtx.INNER());

        // identifier name
        String identifierName = ctx.identifierName().getText();

        // superClassList
        List<String> superClasses = new ArrayList<>();

        if (ctx.superClassList() != null) {
            for (TypeContext tc : ctx.superClassList().typeList().type()) {
                superClasses.add(tc.getText());
            }
        }

        // Build and return
        ClassDescriptor.Builder builder = new ClassDescriptor.Builder(identifierName)
                .implementedInterfaces(superClasses);
        staticModifier.ifPresent(builder::staticModifier);
        finalModifier.ifPresent(builder::finalModifier);
        visibilityModifier.ifPresent(builder::visibilityModifier);
        abstractModifier.ifPresent(builder::abstractModifier);
        strictfpModifier.ifPresent(builder::strictfpModifier);
        interfaceModifier.ifPresent(builder::interfaceModifier);
        strictfpModifier.ifPresent(builder::strictfpModifier);
        innerModifier.ifPresent(builder::inner);
        anonymousModifier.ifPresent(builder::anonymous);
        return new TargetDescriptor(Optional.of(searchType), builder.build());
    }

    private static TargetDescriptor parseMethod(MethodContext ctx) {
        // LanguageElementHeader
        CommonModifiersContext commonModsCtx = ctx.commonModifiers();
        SearchType searchType = parseSearchType(commonModsCtx.DEF(), commonModsCtx.USE());
        Optional<VisibilityModifier> visibilityModifier = commonModsCtx.visibilityModifier() != null
                ? visibilityModifier(commonModsCtx.visibilityModifier()) : Optional.empty();

        // Other modifiers
        Optional<Boolean> staticModifier = parseOptionalTrueOrEmpty(commonModsCtx.STATIC());
        Optional<Boolean> finalModifier = parseOptionalTrueOrEmpty(commonModsCtx.FINAL());

        // overridden
        Optional<Boolean> overriddenModifier = parseOptionalTrueOrEmpty(ctx.OVERRIDDEN());
        Optional<String> returnType = parseTextOrEmpty(ctx.type());

        // identifier name
        String identifierName = ctx.identifierName().getText();

        // methodParameters
        Optional<Integer> parameterCount = Optional.empty();
        Optional<Boolean> hasParameters = Optional.empty();
        List<ParameterVariableDescriptor> parameters = new ArrayList<>();

        if (ctx.methodParameters() != null) {
            MethodParametersContext paramsCtx = ctx.methodParameters();

            if (paramsCtx.NUMBER() != null) {
                hasParameters = Optional.of(true);
                int count = Integer.parseInt(paramsCtx.NUMBER().getText());
                parameterCount = Optional.of(count);
            } else if (paramsCtx.paramTypeList() != null) {
                hasParameters = Optional.of(true);
                ParamTypeListContext pnt = paramsCtx.paramTypeList();
                parameters.add(parseParameterVariableDescriptor(pnt.type(), pnt.FINAL()));

                for (ParamTypeListRestContext p : pnt.paramTypeListRest()) {
                    parameters.add(parseParameterVariableDescriptor(p.type(), p.FINAL()));
                }
            } else if (paramsCtx.paramNamedTypeList() != null) {
                hasParameters = Optional.of(true);
                ParamNamedTypeListContext ntlc = paramsCtx.paramNamedTypeList();
                parameters.add(parseParameterVariableDescriptor(ntlc.identifierName(), ntlc.type(), ntlc.FINAL()));

                for (ParamNamedTypeListRestContext p : ntlc.paramNamedTypeListRest()) {
                    parameters.add(parseParameterVariableDescriptor(p.identifierName(), p.type(), p.FINAL()));
                }
            }
        }

        // methodThrownExceptions
        Optional<Boolean> hasThrownExceptions = Optional.empty();
        List<String> thrownExceptions = new ArrayList<>();

        if (ctx.methodThrownExceptions() != null) {
            hasThrownExceptions = Optional.of(true);

            for (TypeContext type : ctx.methodThrownExceptions().typeList().type()) {
                thrownExceptions.add(type.getText());
            }
        }

        // Build and return
        MethodDescriptor.Builder builder = new MethodDescriptor.Builder(identifierName)
                .parameters(parameters)
                .thrownExceptions(thrownExceptions);
        staticModifier.ifPresent(builder::staticModifier);
        finalModifier.ifPresent(builder::finalModifier);
        overriddenModifier.ifPresent(builder::overridden);
        visibilityModifier.ifPresent(builder::visibilityModifier);
        returnType.ifPresent(builder::returnType);
        parameterCount.ifPresent(builder::parameterCount);
        hasParameters.ifPresent(builder::hasParameters);
        hasThrownExceptions.ifPresent(builder::hasThrownExceptions);
        return new TargetDescriptor(Optional.of(searchType), builder.build());
    }

    private static ParameterVariableDescriptor parseParameterVariableDescriptor(TypeContext typeCtx,
            TerminalNode finalNode) {
        Optional<Boolean> isFinal = finalNode != null ? Optional.of(true) : Optional.empty();
        return new ParameterVariableDescriptor(Optional.empty(), Optional.of(typeCtx.getText()), isFinal);
    }

    private static ParameterVariableDescriptor parseParameterVariableDescriptor(IdentifierNameContext identifierNameCtx,
            TypeContext typeCtx, TerminalNode finalNode) {
        Optional<Boolean> isFinal = finalNode != null ? Optional.of(true) : Optional.empty();
        return new ParameterVariableDescriptor(Optional.of(identifierNameCtx.getText()),
                Optional.of(typeCtx.getText()), isFinal);
    }

    private static TargetDescriptor parseVariable(VariableContext ctx) {
        if (ctx.instanceVariable() != null) {
            InstanceVariableContext ictx = ctx.instanceVariable();
            CommonModifiersContext common = ictx.commonModifiers();
            InstanceVariableDescriptor.Builder builder = new InstanceVariableDescriptor
                    .Builder(ictx.identifierName().getText());
            if (ictx.type() != null)
                builder.identifierType(ictx.type().getText());
            visibilityModifier(common.visibilityModifier()).ifPresent(builder::visibilityModifier);
            parseOptionalTrueOrEmpty(common.STATIC()).ifPresent(f -> builder.staticModifier(true));
            parseOptionalTrueOrEmpty(common.FINAL()).ifPresent(f -> builder.finalModifier(true));
            SearchType t = parseSearchType(ictx.commonModifiers().DEF(), ictx.commonModifiers().USE());
            return new TargetDescriptor(Optional.of(t), builder.build());
        } else if (ctx.localVariable() != null) {
            LocalVariableContext lctx = ctx.localVariable();
            Descriptor descriptor = new LocalVariableDescriptor(lctx.identifierName().getText(),
                    parseTextOrEmpty(lctx.type()), parseOptionalTrueOrEmpty(lctx.FINAL()));
            return new TargetDescriptor(Optional.of(parseSearchType(lctx.DEF(), lctx.USE())), descriptor);
        } else { // param
            ParamVariableContext pctx = ctx.paramVariable();
            ParameterVariableDescriptor.Builder builder = new ParameterVariableDescriptor.Builder()
                    .identifierName(pctx.identifierName().getText());
            parseTextOrEmpty(pctx.type()).ifPresent(builder::identifierType);
            parseOptionalTrueOrEmpty(pctx.FINAL()).ifPresent(f -> builder.finalModifier(true));
            return new TargetDescriptor(Optional.of(parseSearchType(pctx.DEF(), pctx.USE())), builder.build());
        }
    }

    private static TargetDescriptor parseConditional(ControlFlowContext ctx) {
        Descriptor descriptor;

        if (ctx.if0() != null) {
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.IF, Optional.empty(),
                    parseTextOrEmpty(ctx.if0().expr()));
        } else if (ctx.switch0() != null) {
            descriptor = parseControlflowDescriptor(ConditionalDescriptor.Type.SWITCH,
                    ctx.switch0().identifierName(), ctx.switch0().expr());
        } else if (ctx.while0() != null) {
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.WHILE, Optional.empty(),
                    parseTextOrEmpty(ctx.while0().expr()));
        } else if (ctx.dowhile() != null) {
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.DO_WHILE, Optional.empty(),
                    parseTextOrEmpty(ctx.dowhile().expr()));
        } else if (ctx.for0() != null) {
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.FOR, Optional.empty(), Optional.empty());
        } else if (ctx.foreach() != null) {
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.FOR_EACH,
                    parseTextOrEmpty(ctx.foreach().identifierName()), Optional.empty());
        } else { // ternary
            descriptor = new ConditionalDescriptor(ConditionalDescriptor.Type.TERNARY, Optional.empty(),
                    Optional.empty());
        }
        return new TargetDescriptor(descriptor);
    }

    private static TargetDescriptor parseStatement(StatementContext ctx) {
        Descriptor descriptor;
        // synchronized
        descriptor = parseControlflowDescriptor(ConditionalDescriptor.Type.SYNCHRONIZED,
                ctx.synchronized0().identifierName(), ctx.synchronized0().expr());
        return new TargetDescriptor(descriptor);
    }

    private static ConditionalDescriptor parseControlflowDescriptor(ConditionalDescriptor.Type type,
            IdentifierNameContext identifierNameCtx, ExprContext expressionCtx) {
        return new ConditionalDescriptor(type, parseTextOrEmpty(identifierNameCtx), parseTextOrEmpty(expressionCtx));
    }

    private static TargetDescriptor parseComment(CommentContext ctx) {
        if (ctx.singleLineComment() != null) {
            SingleLineCommentContext sctx = ctx.singleLineComment();
            return new TargetDescriptor(new LineCommentDescriptor(parseTextOrEmpty(sctx.content())));
        } else { // multi-line
            MultiLineCommentContext mctx = ctx.multiLineComment();
            Descriptor desc = new BlockCommentDescriptor(parseTextOrEmpty(mctx.content()),
                    parseOptionalTrueOrEmpty(mctx.JAVADOC()));
            return new TargetDescriptor(desc);
        }
    }

    private static Optional<VisibilityModifier> visibilityModifier(VisibilityModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
            return Optional.of(VisibilityModifier.PUBLIC);
        } else if (ctx.PRIVATE() != null) {
            return Optional.of(VisibilityModifier.PRIVATE);
        } else if (ctx.PROTECTED() != null) {
            return Optional.of(VisibilityModifier.PROTECTED);
        } else { // package private
            return Optional.of(VisibilityModifier.PACKAGE_PRIVATE);
        }
    }

    private static RefactorDescriptor parseRefactorDescriptor(RefactorDescriptorContext ctx) {
        if (ctx.changeParameters() != null) {
            List<ParameterVariableDescriptor> parameters = new ArrayList<>();
            ChangeParametersContext cpc = ctx.changeParameters();

            if (cpc.typeList() != null) {
                for (TypeContext type : cpc.typeList().type()) {
                    parameters.add(new ParameterVariableDescriptor(Optional.empty(), Optional.of(type.getText()),
                            Optional.empty()));
                }
            } else { // namedTypeList
                NamedTypeListContext params = cpc.namedTypeList();

                if (params.type().size() != params.identifierName().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < params.type().size(); i++) {
                    parameters.add(parseParameterVariableDescriptor(params.identifierName(i), params.type(i), null));
                }
            }
            return new RefactorDescriptor.ChangeParameters(parameters);
        } else { // rename
            return new RefactorDescriptor.Rename(ctx.rename().identifierName().getText());
        }
    }

    @Override
    public void enterCsarQuery(CsarQueryContext ctx) {
        searchTarget = parseTargetDescriptor(ctx.statementDescriptor());
    }

    @Override
    public void enterContainsQuery(ContainsQueryContext ctx) {
        // Logical operators
        if (ctx.NOT() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperator(LogicalOperator.NOT));
        }

        // Language element
        TargetDescriptor targetDescriptor = parseTargetDescriptor(ctx.statementDescriptor());
        containsQueryElements.add(new ContainsQueryElement.TargetDescriptor(targetDescriptor));
    }

    @Override
    public void enterContainsQueryRest(ContainsQueryRestContext ctx) {
        // Logical operators
        if (ctx.AND() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperator(LogicalOperator.AND));
        } else if (ctx.OR() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperator(LogicalOperator.OR));
        }

        if (ctx.NOT() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperator(LogicalOperator.NOT));
        }

        // Language element
        TargetDescriptor Descriptor = parseTargetDescriptor(ctx.statementDescriptor());
        containsQueryElements.add(new ContainsQueryElement.TargetDescriptor(Descriptor));
    }

    @Override
    public void exitContainsQuery(ContainsQueryContext ctx) {
        ContainsQuery containsQuery = new ContainsQuery(containsQueryElements);

        if (!ContainsQuery.validate(containsQuery)) {
            throw new RuntimeException("invalid contains query elements");
        }
        this.containsQuery = Optional.of(containsQuery);
    }

    @Override
    public void enterFromQuery(FromQueryContext ctx) {
        for (TypeContext type : ctx.typeList().type()) {
            fromTarget.add(type.getText());
        }
    }

    @Override
    public void enterRefactorQuery(RefactorQueryContext ctx) {
        RefactorDescriptor re = parseRefactorDescriptor(ctx.refactorDescriptor());
        refactorDescriptor = Optional.of(re);

        if (!re.validate(searchTarget)) {
            throw new RuntimeException("invalid refactor element");
        }
    }

    public CsarQuery csarQuery() {
        return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorDescriptor);
    }
}
