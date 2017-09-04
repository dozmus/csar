package org.qmul.csar.query;

import grammars.csar.CsarLexer;
import grammars.csar.CsarParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qmul.csar.util.ThrowRuntimeExceptionErrorListener;

public final class CsarQueryFactory {

    /**
     * Creates a {@link CsarQuery} from the textual representation of it (in csar query language syntax).
     * @param query the query
     * @throws IllegalArgumentException if the query is the empty string
     * @throws RuntimeException if the query does not adhere to the syntax of the csar query language
     * @return CsarQuery representing the String query
     */
    public static CsarQuery parse(String query) throws IllegalArgumentException {
        if (query.length() == 0) // NOTE suppresses the following msg: line 1:0 no viable alternative at input '<EOF>'
            throw new IllegalArgumentException("csar query input is empty");

        // Prepare parser
        CsarLexer lexer = new CsarLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CsarParser parser = new CsarParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy()); // terminate parsing early if a parsing error occurs
        parser.addErrorListener(new ThrowRuntimeExceptionErrorListener("csar query")); // throw runtime exception if a parsing error

        // Generate and return csar query
        ParseTreeWalker walker = new ParseTreeWalker();
        CsarQueryGenerator gen = new CsarQueryGenerator();
        walker.walk(gen, parser.csarQuery());
        return gen.csarQuery();
    }

}
