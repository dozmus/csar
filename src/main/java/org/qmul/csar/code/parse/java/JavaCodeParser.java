package org.qmul.csar.code.parse.java;

import grammars.java8pt.JavaLexer;
import grammars.java8pt.JavaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qmul.csar.code.parse.CodeParser;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.ThrowRuntimeExceptionErrorListener;

import java.io.IOException;
import java.nio.file.Path;

public final class JavaCodeParser implements CodeParser {

    /**
     * Parses the argument into a {@link Statement}.
     *
     * @param file the file to parse
     * @return the file as a {@link Statement}
     * @throws RuntimeException if syntax error is encountered in the file
     * @throws IOException if an I/O exception occurs
     */
    @Override
    public Statement parse(Path file) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromPath(file));
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.addErrorListener(new ThrowRuntimeExceptionErrorListener("java"));

        JavaParser parser = new JavaParser(new CommonTokenStream(lexer));
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(new ThrowRuntimeExceptionErrorListener("java"));

        // Generate the code tree for it
        ParseTreeWalker walker = new ParseTreeWalker();
        JavaCodeGenerator gen = new JavaCodeGenerator();
        gen.setPath(file);
        walker.walk(gen, parser.compilationUnit());
        return gen.getRootStatement();
    }

    @Override
    public boolean accepts(Path file) {
        // XXX ignores package-info.java
        String fileName = file.getFileName().toString();
        return !fileName.equals("package-info.java") && fileName.endsWith(".java");
    }
}
