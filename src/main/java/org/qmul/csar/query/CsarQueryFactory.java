package org.qmul.csar.query;

import grammars.csar.CsarLexer;
import grammars.csar.CsarParser;
import grammars.csar.CsarParserListener;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.qmul.csar.query.domain.LanguageElement;
import org.qmul.csar.query.domain.VisibilityModifier;

import java.util.BitSet;
import java.util.Optional;

public final class CsarQueryFactory {

    /**
     * Creates a {@link CsarQuery} from the provided textual representation of it.
     * @throws IllegalArgumentException
     * @throws RuntimeException
     */
    public static CsarQuery parse(String query) throws IllegalArgumentException {
        // Prepare parser
        CsarLexer lexer = new CsarLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CsarParser parser = new CsarParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy()); // terminate parsing early if a parsing error occurs
        parser.addErrorListener(new ErrorListener()); // throw runtime exception if a parsing error

        // Generate and return csar query
        ParseTreeWalker walker = new ParseTreeWalker();
        CsarQueryGenerator gen = new CsarQueryGenerator();
        walker.walk(gen, parser.csarQuery());
        return gen.csarQuery();
    }

    /**
     * The {@link CsarQuery} generator.
     */
    private static final class CsarQueryGenerator implements CsarParserListener {

        private final LanguageElement languageElement = new LanguageElement();

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
        public void enterLanguageElement(CsarParser.LanguageElementContext ctx) {
        }

        @Override
        public void exitLanguageElement(CsarParser.LanguageElementContext ctx) {
        }

        @Override
        public void enterClazz(CsarParser.ClazzContext ctx) {
            languageElement.setType(LanguageElement.Type.CLASS);
            languageElement.setIdentifierName(ctx.IDENTIFIER_NAME().getText());
        }

        @Override
        public void exitClazz(CsarParser.ClazzContext ctx) {
        }

        @Override
        public void enterMethod(CsarParser.MethodContext ctx) {
            languageElement.setType(LanguageElement.Type.METHOD);
            languageElement.setIdentifierName(ctx.IDENTIFIER_NAME().getText());
        }

        @Override
        public void exitMethod(CsarParser.MethodContext ctx) {
        }

        @Override
        public void enterLanguageElementHeader(CsarParser.LanguageElementHeaderContext ctx) {
            if (ctx.DEF() != null) {
                languageElement.setSearchType(CsarQuery.Type.DEFINITION);
            } else if (ctx.USE() != null) {
                languageElement.setSearchType(CsarQuery.Type.USAGE);
            }

            if (ctx.STATIC() != null) {
                languageElement.setStaticModifier(Optional.of(true));
            }

            if (ctx.FINAL() != null) {
                languageElement.setFinalModifier(Optional.of(true));
            }
        }

        @Override
        public void exitLanguageElementHeader(CsarParser.LanguageElementHeaderContext ctx) {
        }

        @Override
        public void enterVisibilityModifier(CsarParser.VisibilityModifierContext ctx) {
            if (ctx.PUBLIC() != null) {
                languageElement.setVisibilityModifier(VisibilityModifier.PUBLIC);
            } else if (ctx.PRIVATE() != null) {
                languageElement.setVisibilityModifier(VisibilityModifier.PRIVATE);
            } else if (ctx.PROTECTED() != null) {
                languageElement.setVisibilityModifier(VisibilityModifier.PROTECTED);
            } else if (ctx.PACKAGE_PRIVATE() != null) {
                languageElement.setVisibilityModifier(VisibilityModifier.PACKAGE_PRIVATE);
            }
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

        public CsarQuery csarQuery() {
            return new CsarQuery(languageElement);
        }
    }

    /**
     * An error listener which throws a {@link RuntimeException} when
     * {@link ANTLRErrorListener#syntaxError(Recognizer, Object, int, int, String, RecognitionException)} is invoked.
     */
    private static class ErrorListener implements ANTLRErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            throw new RuntimeException("syntax error parsing csar query at line " + line);
        }

        @Override
        public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                                    BitSet ambigAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                                BitSet conflictingAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                             int prediction, ATNConfigSet configs) {
        }
    }
}
