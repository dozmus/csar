package org.qmul.csar.code;

import grammars.java8pt.JavaLexer;
import grammars.java8pt.JavaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Path;

public final class JavaCodeTreeParser implements CodeTreeParser {

    @Override
    public Node parse(Path file) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromPath(file));
        JavaParser parser = new JavaParser(new CommonTokenStream(lexer));

        // Generate the code tree for it
        ParseTreeWalker walker = new ParseTreeWalker();
        JavaCodeTreeGenerator gen = new JavaCodeTreeGenerator();
        walker.walk(gen, parser.compilationUnit());
        return gen.getRoot();
    }

    @Override
    public boolean accepts(Path file) {
        return file.getFileName().endsWith(".java");
    }
}
