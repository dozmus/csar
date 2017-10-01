package org.qmul.csar.code.parse.java;

import grammars.java8pt.JavaLexer;
import grammars.java8pt.JavaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qmul.csar.code.parse.CodeParser;
import org.qmul.csar.lang.Statement;

import java.io.IOException;
import java.nio.file.Path;

public final class JavaCodeParser implements CodeParser {

    /**
     * Parses the argument into a {@link Statement}.
     *
     * @param file the file to parse
     * @return the file as a {@link Statement}
     * @throws RuntimeException if unhandled top-level element is encountered in the file
     * @throws IOException if an I/O exception occurs
     */
    @Override
    public Statement parse(Path file) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromPath(file));
        JavaParser parser = new JavaParser(new CommonTokenStream(lexer));

        // Generate the code tree for it
        ParseTreeWalker walker = new ParseTreeWalker();
        JavaCodeGenerator gen = new JavaCodeGenerator();
        walker.walk(gen, parser.compilationUnit());
        return gen.getRootStatement();
    }

    @Override
    public boolean accepts(Path file) {
        return file.getFileName().endsWith(".java");
    }
}
