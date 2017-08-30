package org.qmul.csar.query;

import grammars.csar.CsarLexer;
import grammars.csar.CsarParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qmul.csar.util.ThrowRuntimeExceptionErrorListener;

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
        parser.addErrorListener(new ThrowRuntimeExceptionErrorListener("csar query")); // throw runtime exception if a parsing error

        // Generate and return csar query
        ParseTreeWalker walker = new ParseTreeWalker();
        CsarQueryGenerator gen = new CsarQueryGenerator();
        walker.walk(gen, parser.csarQuery());
        return gen.csarQuery();
    }

}
