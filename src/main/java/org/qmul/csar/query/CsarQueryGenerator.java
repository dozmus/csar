package org.qmul.csar.query;

import grammars.csar.CsarParser;
import grammars.csar.CsarParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link CsarQuery} generator.
 */
class CsarQueryGenerator extends CsarParserBaseListener {

    private LanguageElement searchTarget;
    private List<String> fromTarget = new ArrayList<>();
    private Optional<ContainsQuery> containsQuery = Optional.empty();
    private Optional<RefactorElement> refactorElement = Optional.empty();
    private final List<ContainsQueryElement> containsQueryElements = new ArrayList<>();

    private static CsarQuery.Type parseType(TerminalNode defNode, TerminalNode useNode) {
        if (defNode != null && useNode == null) {
            return CsarQuery.Type.DEF;
        } else if (defNode == null && useNode != null) {
            return CsarQuery.Type.USE;
        }
        throw new RuntimeException("invalid defNode and useNode states");
    }

    /**
     * @return {@code Optional.of(ctx.getText())} if ctx is not null, and <code>Optional.empty()} otherwise.
     */
    private static Optional<String> parseTextOrEmpty(ParserRuleContext ctx) {
        return ctx != null ? Optional.of(ctx.getText()) : Optional.empty();
    }

    /**
     * @return {@code Optional.of(true)} if node is not null, and {@code Optional.empty()} otherwise.
     */
    private static Optional<Boolean> parseOptionalTrueOrEmpty(TerminalNode node) {
        return node != null ? Optional.of(true) : Optional.empty();
    }

    private static LanguageElement parseLanguageElement(CsarParser.LanguageElementContext ctx) {
        if (ctx.clazz() != null) {
            return parseClazz(ctx.clazz());
        } else if (ctx.method() != null) {
            return parseMethod(ctx.method());
        } else if (ctx.variable() != null) {
            return parseVariable(ctx.variable());
        } else if (ctx.controlFlow() != null) {
            return parseControlflow(ctx.controlFlow());
        } else { // comment
            return parseComment(ctx.comment());
        }
    }

    private static LanguageElement parseClazz(CsarParser.ClazzContext ctx) {
        // languageElementHeader
        CsarParser.CommonModifiersContext commonModsCtx = ctx.commonModifiers();
        CsarQuery.Type searchType = parseType(commonModsCtx.DEF(), commonModsCtx.USE());
        Optional<VisibilityModifier> visibilityModifier = commonModsCtx.visibilityModifier() != null
                ? visibilityModifier(commonModsCtx.visibilityModifier()) : Optional.empty();

        // Other modifiers
        Optional<Boolean> staticModifier = parseOptionalTrueOrEmpty(commonModsCtx.STATIC());
        Optional<Boolean> finalModifier = parseOptionalTrueOrEmpty(commonModsCtx.FINAL());

        // classModifiers
        CsarParser.ClassModifiersContext classModsCtx = ctx.classModifiers();
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
            for (CsarParser.TypeContext tc : ctx.superClassList().typeList().type()) {
                superClasses.add(tc.getText());
            }
        }
        return new ClassLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier, identifierName,
                interfaceModifier, abstractModifier, strictfpModifier, anonymousModifier, innerModifier,
                new ArrayList<>(), superClasses);
    }

    private static LanguageElement parseMethod(CsarParser.MethodContext ctx) {
        // languageElementHeader
        CsarParser.CommonModifiersContext commonModsCtx = ctx.commonModifiers();
        CsarQuery.Type searchType = parseType(commonModsCtx.DEF(), commonModsCtx.USE());
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
        List<Parameter> parameters = new ArrayList<>();

        if (ctx.methodParameters() != null) {
            CsarParser.MethodParametersContext mpt = ctx.methodParameters();

            if (mpt.NUMBER() != null) {
                int count = Integer.parseInt(mpt.NUMBER().getText());
                parameterCount = Optional.of(count);
            } else if (mpt.paramTypeList() != null) {
                CsarParser.ParamTypeListContext pnt = mpt.paramTypeList();
                Optional<Boolean> paramFinal = pnt.FINAL() != null ? Optional.of(true) : Optional.empty();
                parameters.add(new Parameter(pnt.type().getText(), Optional.empty(), paramFinal));

                for (CsarParser.ParamTypeListRestContext p : pnt.paramTypeListRest()) {
                    paramFinal = p.FINAL() != null ? Optional.of(true) : Optional.empty();
                    parameters.add(new Parameter(p.type().getText(), Optional.empty(), paramFinal));
                }
            } else if (mpt.paramNamedTypeList() != null) {
                CsarParser.ParamNamedTypeListContext ntlc = mpt.paramNamedTypeList();
                Optional<Boolean> paramFinal = ntlc.FINAL() != null ? Optional.of(true) : Optional.empty();
                parameters.add(new Parameter(ntlc.type().getText(), Optional.of(ntlc.identifierName().getText()),
                        paramFinal));

                for (CsarParser.ParamNamedTypeListRestContext p : ntlc.paramNamedTypeListRest()) {
                    paramFinal = p.FINAL() != null ? Optional.of(true) : Optional.empty();
                    parameters.add(new Parameter(p.type().getText(), Optional.of(p.identifierName().getText()),
                            paramFinal));
                }
            }
        }

        // methodThrownExceptions
        List<String> thrownExceptions = new ArrayList<>();

        if (ctx.methodThrownExceptions() != null) {
            for (CsarParser.TypeContext type : ctx.methodThrownExceptions().typeList().type()) {
                thrownExceptions.add(type.getText());
            }
        }

        // superClassList
        List<String> superClasses = new ArrayList<>();

        if (ctx.superClassList() != null) {
            for (CsarParser.TypeContext type : ctx.superClassList().typeList().type()) {
                superClasses.add(type.getText());
            }
        }
        return new MethodLanguageElement(searchType, visibilityModifier, staticModifier, finalModifier, identifierName,
                returnType, overriddenModifier, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), parameterCount, parameters, thrownExceptions, superClasses);
    }

    private static LanguageElement parseVariable(CsarParser.VariableContext ctx) {
        if (ctx.instanceVariable() != null) {
            CsarParser.InstanceVariableContext ictx = ctx.instanceVariable();
            CsarParser.CommonModifiersContext common = ictx.commonModifiers();
            return new VariableLanguageElement.InstanceVariableLanguageElement(
                    parseType(ictx.commonModifiers().DEF(), ictx.commonModifiers().USE()),
                    visibilityModifier(common.visibilityModifier()),
                    parseOptionalTrueOrEmpty(common.STATIC()),
                    parseOptionalTrueOrEmpty(common.FINAL()),
                    ictx.identifierName().getText(),
                    parseTextOrEmpty(ictx.type())
            );
        } else if (ctx.localVariable() != null) {
            CsarParser.LocalVariableContext lctx = ctx.localVariable();
            return new VariableLanguageElement(parseType(lctx.DEF(), lctx.USE()),
                    VariableType.LOCAL,
                    parseOptionalTrueOrEmpty(lctx.FINAL()),
                    lctx.identifierName().getText(),
                    parseTextOrEmpty(lctx.type())
            );
        } else { // param
            CsarParser.ParamVariableContext pctx = ctx.paramVariable();
            return new VariableLanguageElement(parseType(pctx.DEF(), pctx.USE()),
                    VariableType.PARAM,
                    parseOptionalTrueOrEmpty(pctx.FINAL()),
                    pctx.identifierName().getText(),
                    parseTextOrEmpty(pctx.type())
            );
        }
    }

    private static LanguageElement parseControlflow(CsarParser.ControlFlowContext ctx) {
        if (ctx.if0() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowType.IF,
                    parseTextOrEmpty(ctx.if0().expr()));
        } else if (ctx.switch0() != null) {
            return new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                    ControlFlowType.SWITCH,
                    parseTextOrEmpty(ctx.switch0().identifierName()),
                    parseTextOrEmpty(ctx.switch0().expr()));
        } else if (ctx.while0() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowType.WHILE,
                    parseTextOrEmpty(ctx.while0().expr()));
        } else if (ctx.dowhile() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowType.DO_WHILE,
                    parseTextOrEmpty(ctx.dowhile().expr()));
        } else if (ctx.for0() != null) {
            return new ControlFlowLanguageElement(ControlFlowType.FOR);
        } else if (ctx.foreach() != null) {
            return new ControlFlowLanguageElement.NamedControlFlowLanguageElement(
                    ControlFlowType.FOREACH,
                    parseTextOrEmpty(ctx.foreach().identifierName()));
        } else if (ctx.ternary() != null) {
            return new ControlFlowLanguageElement(ControlFlowType.TERNARY);
        } else { // synchronized
            return new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                    ControlFlowType.SYNCHRONIZED,
                    parseTextOrEmpty(ctx.synchronized0().identifierName()),
                    parseTextOrEmpty(ctx.synchronized0().expr()));
        }
    }

    private static LanguageElement parseComment(CsarParser.CommentContext ctx) {
        if (ctx.singleLineComment() != null) {
            CsarParser.SingleLineCommentContext sctx = ctx.singleLineComment();
            return new CommentLanguageElement(CommentType.SINGLE, Optional.empty(),
                    parseTextOrEmpty(sctx.content()));
        } else { // multi-line
            CsarParser.MultiLineCommentContext mctx = ctx.multiLineComment();
            return new CommentLanguageElement(CommentType.MULTI,
                    parseOptionalTrueOrEmpty(mctx.JAVADOC()), parseTextOrEmpty(mctx.content()));
        }
    }

    private static Optional<VisibilityModifier> visibilityModifier(CsarParser.VisibilityModifierContext ctx) {
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

    private static RefactorElement parseRefactorElement(CsarParser.RefactorElementContext ctx) {
        if (ctx.changeParameters() != null) {
            List<Parameter> parameters = new ArrayList<>();
            CsarParser.ChangeParametersContext cpc = ctx.changeParameters();

            if (cpc.typeList() != null) {
                for (CsarParser.TypeContext type : cpc.typeList().type()) {
                    parameters.add(new Parameter(type.getText(), Optional.empty()));
                }
            } else { // namedTypeList
                CsarParser.NamedTypeListContext ntlc = cpc.namedTypeList();

                if (ntlc.type().size() != ntlc.identifierName().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < ntlc.type().size(); i++) {
                    parameters.add(new Parameter(ntlc.type(i).getText(),
                            Optional.of(ntlc.identifierName(i).getText())));
                }
            }
            return new RefactorElement.ChangeParametersRefactorElement(parameters);
        } else { // rename
            return new RefactorElement.RenameRefactorElement(ctx.rename().identifierName().getText());
        }
    }

    @Override
    public void enterCsarQuery(CsarParser.CsarQueryContext ctx) {
        searchTarget = parseLanguageElement(ctx.languageElement());
    }

    @Override
    public void enterContainsQuery(CsarParser.ContainsQueryContext ctx) {
        // Logical operators
        if (ctx.NOT() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(LogicalOperator.NOT));
        }

        // Language element
        LanguageElement languageElement = parseLanguageElement(ctx.languageElement());
        containsQueryElements.add(new ContainsQueryElement.LanguageElementContainsQueryElement(languageElement));
    }

    @Override
    public void enterContainsQueryRest(CsarParser.ContainsQueryRestContext ctx) {
        // Logical operators
        if (ctx.AND() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(LogicalOperator.AND));
        } else if (ctx.OR() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(LogicalOperator.OR));
        }

        if (ctx.NOT() != null) {
            containsQueryElements.add(new ContainsQueryElement.LogicalOperatorContainsQueryElement(LogicalOperator.NOT));
        }

        // Language element
        LanguageElement languageElement = parseLanguageElement(ctx.languageElement());
        containsQueryElements.add(new ContainsQueryElement.LanguageElementContainsQueryElement(languageElement));
    }

    @Override
    public void exitContainsQueryRest(CsarParser.ContainsQueryRestContext ctx) {
        containsQuery = Optional.of(new ContainsQuery(containsQueryElements));
    }

    @Override
    public void enterFromQuery(CsarParser.FromQueryContext ctx) {
        for (CsarParser.TypeContext type : ctx.typeList().type()) {
            fromTarget.add(type.getText());
        }
    }

    @Override
    public void enterRefactorQuery(CsarParser.RefactorQueryContext ctx) {
        refactorElement = Optional.of(parseRefactorElement(ctx.refactorElement()));
    }

    public CsarQuery csarQuery() {
        return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorElement);
    }
}
