package grammars.java8pt;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;
import org.qmul.csar.util.DummyANTLRErrorListener;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

public final class TestJava8PT {

    private static final Path SAMPLES_DIRECTORY = Paths.get("src/test/resources/grammars/java8pt");

    @Test
    public void test() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAMPLES_DIRECTORY)) {
            for (Path path : stream) {
                JavaLexer lexer = new JavaLexer(CharStreams.fromFileName(path.toString()));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                JavaParser parser = new JavaParser(tokens);
                parser.addErrorListener(new DummyANTLRErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                            int charPositionInLine, String msg, RecognitionException e) {
                        fail("RecognitionException thrown in file '" + path.getFileName().toString() + "' at "
                                + line + ":" + charPositionInLine);
                    }
                });
                parser.compilationUnit();
            }
        }
    }
}
