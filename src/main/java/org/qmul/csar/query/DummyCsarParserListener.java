package org.qmul.csar.query;

import grammars.csar.CsarParser;
import grammars.csar.CsarParserListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

class DummyCsarParserListener implements CsarParserListener {

    @Override
    public void enterCsarQuery(CsarParser.CsarQueryContext ctx) {
    }

    @Override
    public void exitCsarQuery(CsarParser.CsarQueryContext ctx) {
    }

    @Override
    public void enterSearchQuery(CsarParser.SearchQueryContext ctx) {
    }

    @Override
    public void exitSearchQuery(CsarParser.SearchQueryContext ctx) {
    }

    @Override
    public void enterDomainQueryPart(CsarParser.DomainQueryPartContext ctx) {
    }

    @Override
    public void exitDomainQueryPart(CsarParser.DomainQueryPartContext ctx) {
    }

    @Override
    public void enterDomainQueryRest(CsarParser.DomainQueryRestContext ctx) {
    }

    @Override
    public void exitDomainQueryRest(CsarParser.DomainQueryRestContext ctx) {
    }

    @Override
    public void enterLanguageElement(CsarParser.LanguageElementContext ctx) {
    }

    @Override
    public void exitLanguageElement(CsarParser.LanguageElementContext ctx) {
    }

    @Override
    public void enterClazz(CsarParser.ClazzContext ctx) {
    }

    @Override
    public void exitClazz(CsarParser.ClazzContext ctx) {
    }

    @Override
    public void enterClassModifiers(CsarParser.ClassModifiersContext ctx) {
    }

    @Override
    public void exitClassModifiers(CsarParser.ClassModifiersContext ctx) {
    }

    @Override
    public void enterSuperClassList(CsarParser.SuperClassListContext ctx) {
    }

    @Override
    public void exitSuperClassList(CsarParser.SuperClassListContext ctx) {
    }

    @Override
    public void enterMethod(CsarParser.MethodContext ctx) {
    }

    @Override
    public void exitMethod(CsarParser.MethodContext ctx) {
    }

    @Override
    public void enterMethodParameters(CsarParser.MethodParametersContext ctx) {
    }

    @Override
    public void exitMethodParameters(CsarParser.MethodParametersContext ctx) {
    }

    @Override
    public void enterMethodThrownExceptions(CsarParser.MethodThrownExceptionsContext ctx) {
    }

    @Override
    public void exitMethodThrownExceptions(CsarParser.MethodThrownExceptionsContext ctx) {
    }

    @Override
    public void enterLanguageElementHeader(CsarParser.LanguageElementHeaderContext ctx) {
    }

    @Override
    public void exitLanguageElementHeader(CsarParser.LanguageElementHeaderContext ctx) {
    }

    @Override
    public void enterTypeList(CsarParser.TypeListContext ctx) {
    }

    @Override
    public void exitTypeList(CsarParser.TypeListContext ctx) {
    }

    @Override
    public void enterNamedTypeList(CsarParser.NamedTypeListContext ctx) {
    }

    @Override
    public void exitNamedTypeList(CsarParser.NamedTypeListContext ctx) {
    }

    @Override
    public void enterType(CsarParser.TypeContext ctx) {
    }

    @Override
    public void exitType(CsarParser.TypeContext ctx) {
    }

    @Override
    public void enterVisibilityModifier(CsarParser.VisibilityModifierContext ctx) {
    }

    @Override
    public void exitVisibilityModifier(CsarParser.VisibilityModifierContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }
}
