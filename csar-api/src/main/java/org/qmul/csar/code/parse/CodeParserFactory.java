package org.qmul.csar.code.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeParserFactory {

    private CodeParser parser;

    public CodeParserFactory(CodeParser parser) {
        this.parser = parser;
    }

    /**
     * Returns a suitable {@link CodeParser} for the argument, or throws an exception.
     *
     * @param file the file for which a {@link CodeParser} is needed
     * @return a suitable {@link CodeParser} for the argument
     * @throws IllegalArgumentException if the argument is a directory, does not exist, or is not handled.
     */
    public CodeParser create(Path file) throws IOException {
        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException("path must not be a directory");
        }

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("file does not exist");
        }

        if (parser.accepts(file)) {
            try {
                return parser.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("unable to create new parser instance");
            }
        }
        throw new IllegalArgumentException("unhanded file");
    }

    /**
     * Returns <tt>true</tt> if the argument is a code file for which a parser exists.
     *
     * @param path the path to check is accepted
     * @return <tt>true</tt> if the given file has a defined parser
     * @see #parser
     */
    public boolean accepts(Path path) {
        return !Files.isDirectory(path) && parser.accepts(path);
    }
}
