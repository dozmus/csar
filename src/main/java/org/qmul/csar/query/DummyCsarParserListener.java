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
    public void enterContainsQuery(CsarParser.ContainsQueryContext ctx) {
    }

    @Override
    public void exitContainsQuery(CsarParser.ContainsQueryContext ctx) {
    }

    @Override
    public void enterContainsQueryRest(CsarParser.ContainsQueryRestContext ctx) {
    }

    @Override
    public void exitContainsQueryRest(CsarParser.ContainsQueryRestContext ctx) {
    }

    @Override
    public void enterFromQuery(CsarParser.FromQueryContext ctx) {
    }

    @Override
    public void exitFromQuery(CsarParser.FromQueryContext ctx) {
    }

    @Override
    public void enterRefactorQuery(CsarParser.RefactorQueryContext ctx) {
    }

    @Override
    public void exitRefactorQuery(CsarParser.RefactorQueryContext ctx) {
    }

    @Override
    public void enterLanguageElement(CsarParser.LanguageElementContext ctx) {
    }

    @Override
    public void exitLanguageElement(CsarParser.LanguageElementContext ctx) {
    }

    @Override
    public void enterRefactorElement(CsarParser.RefactorElementContext ctx) {
    }

    @Override
    public void exitRefactorElement(CsarParser.RefactorElementContext ctx) {
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
    public void enterVariable(CsarParser.VariableContext ctx) {
    }

    @Override
    public void exitVariable(CsarParser.VariableContext ctx) {
    }

    @Override
    public void enterInstanceVariable(CsarParser.InstanceVariableContext ctx) {
    }

    @Override
    public void exitInstanceVariable(CsarParser.InstanceVariableContext ctx) {
    }

    @Override
    public void enterLocalVariable(CsarParser.LocalVariableContext ctx) {
    }

    @Override
    public void exitLocalVariable(CsarParser.LocalVariableContext ctx) {
    }

    @Override
    public void enterParamVariable(CsarParser.ParamVariableContext ctx) {
    }

    @Override
    public void exitParamVariable(CsarParser.ParamVariableContext ctx) {
    }

    @Override
    public void enterControlFlow(CsarParser.ControlFlowContext ctx) {
    }

    @Override
    public void exitControlFlow(CsarParser.ControlFlowContext ctx) {
    }

    @Override
    public void enterIf0(CsarParser.If0Context ctx) {
    }

    @Override
    public void exitIf0(CsarParser.If0Context ctx) {
    }

    @Override
    public void enterSwitch0(CsarParser.Switch0Context ctx) {
    }

    @Override
    public void exitSwitch0(CsarParser.Switch0Context ctx) {
    }

    @Override
    public void enterWhile0(CsarParser.While0Context ctx) {
    }

    @Override
    public void exitWhile0(CsarParser.While0Context ctx) {
    }

    @Override
    public void enterDowhile(CsarParser.DowhileContext ctx) {
    }

    @Override
    public void exitDowhile(CsarParser.DowhileContext ctx) {
    }

    @Override
    public void enterFor0(CsarParser.For0Context ctx) {
    }

    @Override
    public void exitFor0(CsarParser.For0Context ctx) {
    }

    @Override
    public void enterForeach(CsarParser.ForeachContext ctx) {
    }

    @Override
    public void exitForeach(CsarParser.ForeachContext ctx) {
    }

    @Override
    public void enterTernary(CsarParser.TernaryContext ctx) {
    }

    @Override
    public void exitTernary(CsarParser.TernaryContext ctx) {
    }

    @Override
    public void enterSynchronized0(CsarParser.Synchronized0Context ctx) {
    }

    @Override
    public void exitSynchronized0(CsarParser.Synchronized0Context ctx) {
    }

    @Override
    public void enterComment(CsarParser.CommentContext ctx) {
    }

    @Override
    public void exitComment(CsarParser.CommentContext ctx) {
    }

    @Override
    public void enterSingleLineComment(CsarParser.SingleLineCommentContext ctx) {
    }

    @Override
    public void exitSingleLineComment(CsarParser.SingleLineCommentContext ctx) {
    }

    @Override
    public void enterMultiLineComment(CsarParser.MultiLineCommentContext ctx) {
    }

    @Override
    public void exitMultiLineComment(CsarParser.MultiLineCommentContext ctx) {
    }

    @Override
    public void enterRename(CsarParser.RenameContext ctx) {
    }

    @Override
    public void exitRename(CsarParser.RenameContext ctx) {
    }

    @Override
    public void enterChangeParameters(CsarParser.ChangeParametersContext ctx) {
    }

    @Override
    public void exitChangeParameters(CsarParser.ChangeParametersContext ctx) {
    }

    @Override
    public void enterCommonModifiers(CsarParser.CommonModifiersContext ctx) {
    }

    @Override
    public void exitCommonModifiers(CsarParser.CommonModifiersContext ctx) {
    }

    @Override
    public void enterVisibilityModifier(CsarParser.VisibilityModifierContext ctx) {
    }

    @Override
    public void exitVisibilityModifier(CsarParser.VisibilityModifierContext ctx) {
    }

    @Override
    public void enterInstanceVariableModifiers(CsarParser.InstanceVariableModifiersContext ctx) {
    }

    @Override
    public void exitInstanceVariableModifiers(CsarParser.InstanceVariableModifiersContext ctx) {
    }

    @Override
    public void enterType(CsarParser.TypeContext ctx) {
    }

    @Override
    public void exitType(CsarParser.TypeContext ctx) {
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
    public void enterIdentifierName(CsarParser.IdentifierNameContext ctx) {
    }

    @Override
    public void exitIdentifierName(CsarParser.IdentifierNameContext ctx) {
    }

    @Override
    public void enterContent(CsarParser.ContentContext ctx) {
    }

    @Override
    public void exitContent(CsarParser.ContentContext ctx) {
    }

    @Override
    public void enterExpr(CsarParser.ExprContext ctx) {
    }

    @Override
    public void exitExpr(CsarParser.ExprContext ctx) {
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
