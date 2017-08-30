package org.qmul.csar.query;

import grammars.csar.CsarParser;
import org.qmul.csar.query.domain.*;

import java.util.Optional;

/**
 * A {@link CsarQuery} generator.
 */
class CsarQueryGenerator extends DummyCsarParserListener {

    private LanguageElement target = null;
    private String fromTarget = null;
    private DomainQuery domainQuery = null;

    @Override
    public void enterCsarQuery(CsarParser.CsarQueryContext ctx) {
        if (ctx.IDENTIFIER_NAME() != null) {
            fromTarget = ctx.IDENTIFIER_NAME().getText();
        }
    }

    @Override
    public void enterSearchQuery(CsarParser.SearchQueryContext ctx) {
        if (ctx.languageElement() != null) {
            target = parseLanguageElement(ctx.languageElement());
        }

        if (ctx.domainQueryPart() != null) {
            domainQuery = new DomainQuery();
        }
    }

    @Override
    public void enterDomainQueryPart(CsarParser.DomainQueryPartContext ctx) {
        // Logical operators
        if (ctx.NOT() != null) {
            domainQuery.addLogicalOperator(LogicalOperator.NOT);
        }

        // Language element
        domainQuery.addLanguageElement(parseLanguageElement(ctx.languageElement()));
    }

    @Override
    public void enterDomainQueryRest(CsarParser.DomainQueryRestContext ctx) {
        // Logical operators
        if (ctx.AND() != null) {
            domainQuery.addLogicalOperator(LogicalOperator.AND);
        } else if (ctx.OR() != null) {
            domainQuery.addLogicalOperator(LogicalOperator.OR);
        }

        if (ctx.NOT() != null) {
            domainQuery.addLogicalOperator(LogicalOperator.NOT);
        }

        // Language element
        domainQuery.addLanguageElement(parseLanguageElement(ctx.languageElement()));
    }

    private static LanguageElement parseLanguageElement(CsarParser.LanguageElementContext ctx) {
        if (ctx.clazz() != null) {
            return parseClazz(ctx.clazz());
        } else if (ctx.method() != null) {
            return parseMethod(ctx.method());
        }
        throw new RuntimeException("syntax error parsing csar query language element");
    }

    private static LanguageElement parseClazz(CsarParser.ClazzContext ctx) {
        ClassLanguageElement cle = new ClassLanguageElement();

        // languageElementHeader
        applyLanguageElementHeader(cle, ctx.languageElementHeader());

        // classModifiers
        applyClassModifiers(cle, ctx.classModifiers());

        // identifier name
        cle.setIdentifierName(ctx.IDENTIFIER_NAME().getText());

        // superClassList
        if (ctx.superClassList() != null) {
            for (CsarParser.TypeContext tc : ctx.superClassList().typeList().type()) {
                cle.addSuperClass(tc.getText());
            }
        }
        return cle;
    }

    private static void applyLanguageElementHeader(LanguageElement le, CsarParser.LanguageElementHeaderContext ctx) {
        // Search type
        if (ctx.DEF() != null) {
            le.setSearchType(CsarQuery.Type.DEFINITION);
        } else if (ctx.USE() != null) {
            le.setSearchType(CsarQuery.Type.USAGE);
        }

        // Visibility modifier
        if (ctx.visibilityModifier() != null) {
            CsarParser.VisibilityModifierContext vmc = ctx.visibilityModifier();

            if (vmc.PUBLIC() != null) {
                le.setVisibilityModifier(VisibilityModifier.PUBLIC);
            } else if (vmc.PRIVATE() != null) {
                le.setVisibilityModifier(VisibilityModifier.PRIVATE);
            } else if (vmc.PROTECTED() != null) {
                le.setVisibilityModifier(VisibilityModifier.PROTECTED);
            } else if (vmc.PACKAGE_PRIVATE() != null) {
                le.setVisibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
            }
        }

        // Other modifiers
        if (ctx.STATIC() != null) {
            le.setStaticModifier(Optional.of(true));
        }

        if (ctx.FINAL() != null) {
            le.setFinalModifier(Optional.of(true));
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
        applyLanguageElementHeader(mle, ctx.languageElementHeader());

        // overridden
        if (ctx.OVERRIDDEN() != null) {
            mle.setOverridden(Optional.of(true));
        }

        // return type
        if (ctx.type() != null) {
            mle.setReturnType(ctx.type().getText());
        }

        // identifier name
        mle.setIdentifierName(ctx.IDENTIFIER_NAME().getText());

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

                if (ntlc.type().size() != ntlc.IDENTIFIER_NAME().size()) {
                    throw new RuntimeException("syntax error parsing csar query named type list");
                }

                for (int i = 0; i < ntlc.type().size(); i++) {
                    mle.addParameter(new Identifier(ntlc.IDENTIFIER_NAME(i).getText(), ntlc.type(i).getText()));
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

    public CsarQuery csarQuery() {
        return new CsarQuery(target, domainQuery, fromTarget);
    }
}
