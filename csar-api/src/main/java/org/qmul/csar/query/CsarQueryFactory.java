package org.qmul.csar.query;

import grammars.csar.CsarLexer;
import grammars.csar.CsarParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qmul.csar.util.DefaultAntlrErrorListener;

/**
 * A factory for creating instances of {@link CsarQuery}, by parsing a <tt>String</tt> query.
 * @see CsarQueryParser
 */
public final class CsarQueryFactory {

    /**
     * Returns the <tt>String</tt> argument as a {@link CsarQuery}.
     * The parsing is reliant on the ANTLR auto-generated classes {@link CsarLexer} and {@link CsarParser}, and uses
     * the {@link BailErrorStrategy}.
     *
     * @param query the csar query
     * @throws IllegalArgumentException thrown if the query is the empty string
     * @throws RuntimeException thrown if the query has invalid syntax
     * @return the {@link CsarQuery} represented by the argument
     */
    public static CsarQuery parse(String query) throws IllegalArgumentException {
        // XXX the following suppresses a debug message during testing: line 1:0 no viable alternative at input '<EOF>'
        if (query.length() == 0)
            throw new IllegalArgumentException("csar query input is empty");

        // Create and configure parser
        CsarLexer lexer = new CsarLexer(CharStreams.fromString(query));
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.addErrorListener(new DefaultAntlrErrorListener("csar query"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CsarParser parser = new CsarParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(new DefaultAntlrErrorListener("csar query"));
        parser.setErrorHandler(new BailErrorStrategy()); // terminate parsing early if a parsing error occurs

        // Generate and return csar query
        ParseTreeWalker walker = new ParseTreeWalker();
        CsarQueryParser queryParser = new CsarQueryParser();
        walker.walk(queryParser, parser.csarQuery());
        return queryParser.csarQuery();
    }
}
