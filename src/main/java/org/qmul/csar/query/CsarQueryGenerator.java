package org.qmul.csar.query;

import grammars.csar.CsarParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.query.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link CsarQuery} generator.
 */
class CsarQueryGenerator extends DummyCsarParserListener {

    private LanguageElement searchTarget;
    private List<String> fromTarget = new ArrayList<>();
    private ContainsQuery containsQuery;
    private RefactorElement refactorElement;
    private final List<ContainsQueryElement> containsQueryElements = new ArrayList<>();

    private static CsarQuery.Type parseType(TerminalNode defNode, TerminalNode useNode) {
        if (defNode != null && useNode == null) {
            return CsarQuery.Type.DEF;
        } else if (defNode == null && useNode != null) {
            return CsarQuery.Type.USE;
        }
        throw new RuntimeException("invalid defNode and useNode states");
    }

    private static String parseTypeTextOrNull(CsarParser.TypeContext ctx) {
        return ctx != null ? ctx.getText() : null;
    }

    /**
     * @return <code>Optional#of(true)</code> if node is not null, and <code>Optional#empty()</code> otherwise.
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
        VisibilityModifier visibilityModifier = commonModsCtx.visibilityModifier() == null
                ? null : visibilityModifier(commonModsCtx.visibilityModifier());

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
                interfaceModifier, abstractModifier, strictfpModifier, anonymousModifier, innerModifier, superClasses);
    }

    private static LanguageElement parseMethod(CsarParser.MethodContext ctx) {
        // languageElementHeader
        CsarParser.CommonModifiersContext commonModsCtx = ctx.commonModifiers();
        CsarQuery.Type searchType = parseType(commonModsCtx.DEF(), commonModsCtx.USE());
        VisibilityModifier visibilityModifier = commonModsCtx.visibilityModifier() == null
                ? null : visibilityModifier(commonModsCtx.visibilityModifier());

        // Other modifiers
        Optional<Boolean> staticModifier = parseOptionalTrueOrEmpty(commonModsCtx.STATIC());
        Optional<Boolean> finalModifier = parseOptionalTrueOrEmpty(commonModsCtx.FINAL());

        // overridden
        Optional<Boolean> overriddenModifier = parseOptionalTrueOrEmpty(ctx.OVERRIDDEN());
        String returnType = ctx.type() != null ? ctx.type().getText() : null;

        // identifier name
        String identifierName = ctx.identifierName().getText();

        // methodParameters
        Optional<Integer> parameterCount = Optional.empty();
        List<Identifier> parameters = new ArrayList<>();

        if (ctx.methodParameters() != null) {
            CsarParser.MethodParametersContext mpt = ctx.methodParameters();

            if (mpt.NUMBER() != null) {
                int count = Integer.parseInt(mpt.NUMBER().getText());
                parameterCount = Optional.of(count);
            } else if (mpt.typeList() != null) {
                for (CsarParser.TypeContext type : mpt.typeList().type()) {
                    parameters.add(new Identifier(null, type.getText()));
                }
            } else if (mpt.namedTypeList() != null) {
                CsarParser.NamedTypeListContext ntlc = mpt.namedTypeList();

                if (ntlc.type().size() != ntlc.identifierName().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < ntlc.type().size(); i++) {
                    parameters.add(new Identifier(ntlc.identifierName(i).getText(), ntlc.type(i).getText()));
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
                returnType, overriddenModifier, parameterCount, parameters, thrownExceptions, superClasses);
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
                    parseTypeTextOrNull(ictx.type())
            );
        } else if (ctx.localVariable() != null) {
            CsarParser.LocalVariableContext lctx = ctx.localVariable();
            return new VariableLanguageElement(parseType(lctx.DEF(), lctx.USE()),
                    VariableLanguageElement.VariableType.LOCAL,
                    parseOptionalTrueOrEmpty(lctx.FINAL()),
                    lctx.identifierName().getText(),
                    parseTypeTextOrNull(lctx.type())
            );
        } else { // param
            CsarParser.ParamVariableContext pctx = ctx.paramVariable();
            return new VariableLanguageElement(parseType(pctx.DEF(), pctx.USE()),
                    VariableLanguageElement.VariableType.PARAM,
                    parseOptionalTrueOrEmpty(pctx.FINAL()),
                    pctx.identifierName().getText(),
                    parseTypeTextOrNull(pctx.type())
            );
        }
    }

    private static LanguageElement parseControlflow(CsarParser.ControlFlowContext ctx) {
        if (ctx.if0() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.IF,
                    ctx.if0().expr() != null ? ctx.if0().expr().getText() : null);
        } else if (ctx.switch0() != null) {
            return new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.SWITCH,
                    ctx.switch0().identifierName() != null ? ctx.switch0().identifierName().getText() : null,
                    ctx.switch0().expr() != null ? ctx.switch0().expr().getText() : null);
        } else if (ctx.while0() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.WHILE,
                    ctx.while0().expr() != null ? ctx.while0().expr().getText() : null);
        } else if (ctx.dowhile() != null) {
            return new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.DOWHILE,
                    ctx.dowhile().expr() != null ? ctx.dowhile().expr().getText() : null);
        } else if (ctx.for0() != null) {
            return new ControlFlowLanguageElement(ControlFlowLanguageElement.ControlFlowType.FOR);
        } else if (ctx.foreach() != null) {
            return new ControlFlowLanguageElement.NamedControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.FOREACH,
                    ctx.foreach().identifierName() != null ? ctx.foreach().identifierName().getText() : null);
        } else if (ctx.ternary() != null) {
            return new ControlFlowLanguageElement(ControlFlowLanguageElement.ControlFlowType.TERNARY);
        } else { // synchronized
            return new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED,
                    ctx.synchronized0().identifierName() != null ? ctx.synchronized0().identifierName().getText() : null,
                    ctx.synchronized0().expr() != null ? ctx.synchronized0().expr().getText() : null);
        }
    }

    private static LanguageElement parseComment(CsarParser.CommentContext ctx) {
        if (ctx.singleLineComment() != null) {
            CsarParser.SingleLineCommentContext sctx = ctx.singleLineComment();
            return new CommentLanguageElement(CommentLanguageElement.CommentType.SINGLE, Optional.empty(),
                    sctx.content().getText());
        } else { // multi-line
            CsarParser.MultiLineCommentContext mctx = ctx.multiLineComment();
            return new CommentLanguageElement(CommentLanguageElement.CommentType.MULTI,
                    parseOptionalTrueOrEmpty(mctx.JAVADOC()), mctx.content().getText());
        }
    }

    private static VisibilityModifier visibilityModifier(CsarParser.VisibilityModifierContext ctx) {
        if (ctx.PUBLIC() != null) {
            return VisibilityModifier.PUBLIC;
        } else if (ctx.PRIVATE() != null) {
            return VisibilityModifier.PRIVATE;
        } else if (ctx.PROTECTED() != null) {
            return VisibilityModifier.PROTECTED;
        } else { // package private
            return VisibilityModifier.PACKAGE_PRIVATE;
        }
    }

    private static RefactorElement parseRefactorElement(CsarParser.RefactorElementContext ctx) {
        if (ctx.changeParameters() != null) {
            List<Identifier> parameters = new ArrayList<>();
            CsarParser.ChangeParametersContext cpc = ctx.changeParameters();

            if (cpc.typeList() != null) {
                for (CsarParser.TypeContext type : cpc.typeList().type()) {
                    parameters.add(new Identifier(null, type.getText()));
                }
            } else { // namedTypeList
                CsarParser.NamedTypeListContext ntlc = cpc.namedTypeList();

                if (ntlc.type().size() != ntlc.identifierName().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < ntlc.type().size(); i++) {
                    parameters.add(new Identifier(ntlc.identifierName(i).getText(), ntlc.type(i).getText()));
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
        containsQuery = new ContainsQuery(containsQueryElements);
    }

    @Override
    public void enterFromQuery(CsarParser.FromQueryContext ctx) {
        for (CsarParser.TypeContext type : ctx.typeList().type()) {
            fromTarget.add(type.getText());
        }
    }

    @Override
    public void enterRefactorQuery(CsarParser.RefactorQueryContext ctx) {
        refactorElement = parseRefactorElement(ctx.refactorElement());
    }

    public CsarQuery csarQuery() {
        return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorElement);
    }
}
