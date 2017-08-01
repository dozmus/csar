package grammars.java8pt;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestJava8PT {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestJava8PT.class);
    private static final Path SAMPLES_DIRECTORY = Paths.get("src/test/resources/grammars/java8pt");

    @Test
    public void test() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAMPLES_DIRECTORY)) {
            for (Path path : stream) {
                LOGGER.info("Testing: {}", path.getFileName().toString());
                JavaLexer lexer = new JavaLexer(CharStreams.fromFileName(path.toString()));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                JavaParser parser = new JavaParser(tokens);
                parser.compilationUnit();
            }
        }
    }
}
