package org.qmul.csar.query;

import grammars.csar.CsarParser;
import org.qmul.csar.query.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link CsarQuery} generator.
 */
class CsarQueryGenerator extends DummyCsarParserListener {

    private LanguageElement searchTarget;
    private List<String> fromTarget;
    private ContainsQuery containsQuery;
    private RefactorElement refactorElement;

    @Override
    public void enterCsarQuery(CsarParser.CsarQueryContext ctx) {
        if (ctx.containsQuery() != null) {
            containsQuery = new ContainsQuery();
        }

        if (ctx.fromQuery() != null) {
            fromTarget = new ArrayList<>();
        }
        searchTarget = parseLanguageElement(ctx.languageElement());
    }

    @Override
    public void enterContainsQuery(CsarParser.ContainsQueryContext ctx) {
        // Logical operators
        if (ctx.NOT() != null) {
            containsQuery.addLogicalOperator(LogicalOperator.NOT);
        }

        // Language element
        containsQuery.addLanguageElement(parseLanguageElement(ctx.languageElement()));
    }

    @Override
    public void enterContainsQueryRest(CsarParser.ContainsQueryRestContext ctx) {
        // Logical operators
        if (ctx.AND() != null) {
            containsQuery.addLogicalOperator(LogicalOperator.AND);
        } else if (ctx.OR() != null) {
            containsQuery.addLogicalOperator(LogicalOperator.OR);
        }

        if (ctx.NOT() != null) {
            containsQuery.addLogicalOperator(LogicalOperator.NOT);
        }

        // Language element
        containsQuery.addLanguageElement(parseLanguageElement(ctx.languageElement()));
    }

    @Override
    public void enterFromQuery(CsarParser.FromQueryContext ctx) {
        for (CsarParser.TypeContext type : ctx.typeList().type()) {
            fromTarget.add(type.getText());
        }
    }

    @Override
    public void enterRefactorQuery(CsarParser.RefactorQueryContext ctx) {
        // TODO refactorElement = ...
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
        ClassLanguageElement cle = new ClassLanguageElement();

        // languageElementHeader
        applyCommonModifiers(cle.getCommonModifiers(), ctx.commonModifiers());

        // classModifiers
        applyClassModifiers(cle, ctx.classModifiers());

        // identifier name
        cle.setIdentifierName(ctx.identifierName().getText());

        // superClassList
        if (ctx.superClassList() != null) {
            for (CsarParser.TypeContext tc : ctx.superClassList().typeList().type()) {
                cle.addSuperClass(tc.getText());
            }
        }
        return cle;
    }

    private static void applyCommonModifiers(CommonModifiers commonModifiers, CsarParser.CommonModifiersContext ctx) {
        // Search type
        if (ctx.DEF() != null) {
            commonModifiers.setSearchType(CsarQuery.Type.DEFINITION);
        } else if (ctx.USE() != null) {
            commonModifiers.setSearchType(CsarQuery.Type.USAGE);
        }

        // Visibility modifier
        if (ctx.visibilityModifier() != null) {
            CsarParser.VisibilityModifierContext vmc = ctx.visibilityModifier();

            if (vmc.PUBLIC() != null) {
                commonModifiers.setVisibilityModifier(VisibilityModifier.PUBLIC);
            } else if (vmc.PRIVATE() != null) {
                commonModifiers.setVisibilityModifier(VisibilityModifier.PRIVATE);
            } else if (vmc.PROTECTED() != null) {
                commonModifiers.setVisibilityModifier(VisibilityModifier.PROTECTED);
            } else if (vmc.PACKAGE_PRIVATE() != null) {
                commonModifiers.setVisibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
            }
        }

        // Other modifiers
        if (ctx.STATIC() != null) {
            commonModifiers.setStaticModifier(Optional.of(true));
        }

        if (ctx.FINAL() != null) {
            commonModifiers.setFinalModifier(Optional.of(true));
        }
    }

    private static void applyClassModifiers(ClassLanguageElement cle, CsarParser.ClassModifiersContext ctx) {
        if (ctx.ABSTRACT() != null) {
            cle.setAbstractModifier(Optional.of(true));
        }

        if (ctx.INTERFACE() != null) {
            cle.setInterfaceModifier(Optional.of(true));
        }

        if (ctx.STRICTFP() != null) {
            cle.setStrictfpModifier(Optional.of(true));
        }

        if (ctx.ANONYMOUS() != null) {
            cle.setAnonymous(Optional.of(true));
        }

        if (ctx.INNER() != null) {
            cle.setInner(Optional.of(true));
        }
    }

    private static LanguageElement parseMethod(CsarParser.MethodContext ctx) {
        MethodLanguageElement mle = new MethodLanguageElement();

        // languageElementHeader
        applyCommonModifiers(mle.getCommonModifiers(), ctx.commonModifiers());

        // overridden
        if (ctx.OVERRIDDEN() != null) {
            mle.setOverridden(Optional.of(true));
        }

        // return type
        if (ctx.type() != null) {
            mle.setReturnType(ctx.type().getText());
        }

        // identifier name
        mle.setIdentifierName(ctx.identifierName().getText());

        // methodParameters
        if (ctx.methodParameters() != null) {
            CsarParser.MethodParametersContext mpt = ctx.methodParameters();

            if (mpt.NUMBER() != null) {
                mle.setParameterCount(Optional.of(Integer.parseInt(mpt.NUMBER().getText())));
            } else if (mpt.typeList() != null) {
                for (CsarParser.TypeContext type : mpt.typeList().type()) {
                    mle.addParameter(new Identifier(null, type.getText()));
                }
            } else if (mpt.namedTypeList() != null) {
                CsarParser.NamedTypeListContext ntlc = mpt.namedTypeList();

                if (ntlc.type().size() != ntlc.identifierName().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < ntlc.type().size(); i++) {
                    mle.addParameter(new Identifier(ntlc.identifierName(i).getText(), ntlc.type(i).getText()));
                }
            }
        }

        // methodThrownExceptions
        if (ctx.methodThrownExceptions() != null) {
            for (CsarParser.TypeContext type : ctx.methodThrownExceptions().typeList().type()) {
                mle.addThrownException(type.getText());
            }
        }

        // superClassList
        if (ctx.superClassList() != null) {
            for (CsarParser.TypeContext type : ctx.superClassList().typeList().type()) {
                mle.addSuperClass(type.getText());
            }
        }
        return mle;
    }

    private static LanguageElement parseVariable(CsarParser.VariableContext ctx) {
        if (ctx.instanceVariable() != null) {
            return null; // TODO impl
        } else if (ctx.localVariable() != null) {
            CsarParser.LocalVariableContext lctx = ctx.localVariable();
            return new VariableLanguageElement(VariableLanguageElement.VariableType.LOCAL,
                    lctx.identifierName().getText(), Optional.of(lctx.FINAL() != null));
        } else { // param
            CsarParser.ParamVariableContext pctx = ctx.paramVariable();
            return new VariableLanguageElement(VariableLanguageElement.VariableType.PARAM,
                    pctx.identifierName().getText(), Optional.of(pctx.FINAL() != null));
        }
    }

    private static LanguageElement parseControlflow(CsarParser.ControlFlowContext ctx) {
        if (ctx.if0() != null) {

        } else if (ctx.switch0() != null) {

        } else if (ctx.while0() != null) {

        } else if (ctx.dowhile() != null) {

        } else if (ctx.for0() != null) {
            return new ControlFlowLanguageElement(ControlFlowLanguageElement.ControlFlowType.FOR);
        } else if (ctx.foreach() != null) {
            ControlFlowLanguageElement foreach =  new ControlFlowLanguageElement(
                    ControlFlowLanguageElement.ControlFlowType.FOREACH);
            if (ctx.foreach().identifierName() != null)
                foreach.setIdentifierName(ctx.foreach().identifierName().getText());
        } else if (ctx.ternary() != null) {
            return new ControlFlowLanguageElement(ControlFlowLanguageElement.ControlFlowType.TERNARY);
        } else { // synchronized

        }
        return null; // TODO impl
    }

    private static LanguageElement parseComment(CsarParser.CommentContext ctx) {
        if (ctx.singleLineComment() != null) {
            CsarParser.SingleLineCommentContext sctx = ctx.singleLineComment();
            return new CommentLanguageElement(CommentLanguageElement.CommentType.SINGLE,false, sctx.CONTENT().getText());
        } else { // multi-line
            CsarParser.MultiLineCommentContext mctx = ctx.multiLineComment();
            return new CommentLanguageElement(CommentLanguageElement.CommentType.MULTI, mctx.JAVADOC() != null,
                    mctx.CONTENT().getText());
        }
    }

    public CsarQuery csarQuery() {
        return new CsarQuery(searchTarget, containsQuery, fromTarget, refactorElement);
    }
}
