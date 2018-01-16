package org.qmul.csar.code.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A factory for a specific {@link CodeParser}.
 */
public class CodeParserFactory {

    private final Class<? extends CodeParser> parserClass;
    private final CodeParser defaultParser;

    /**
     * Creates a new {@link CodeParserFactory}.
     *
     * @param parserClass the parser to create instances and return of
     * @throws InstantiationException if the parser class cannot be instantiated
     * @throws IllegalAccessException if the parser class cannot be accessed
     */
    public CodeParserFactory(Class<? extends CodeParser> parserClass) throws InstantiationException,
            IllegalAccessException {
        this.parserClass = parserClass;
        this.defaultParser = create();
    }

    /**
     * Creates and returns a suitable {@link CodeParser} for the argument, or throws an exception.
     *
     * @param file the file for which a {@link CodeParser} is needed
     * @return a suitable {@link CodeParser} for the argument
     * @throws IllegalArgumentException if the argument is a directory, does not exist, or is not handled.
     * @throws InstantiationException if the parser class cannot be instantiated
     * @throws IllegalAccessException if the parser class cannot be accessed
     */
    public CodeParser create(Path file) throws IOException, InstantiationException, IllegalAccessException {
        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException("path must not be a directory");
        }

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("file does not exist");
        }

        if (defaultParser.accepts(file)) {
            return create();
        }
        throw new IllegalArgumentException("unhanded file");
    }

    /**
     * Returns <tt>true</tt> if the argument is a code file for which a parser exists.
     *
     * @param path the path to check is accepted
     * @return <tt>true</tt> if the given file has a defined parser
     * @see #defaultParser
     */
    public boolean accepts(Path path) {
        return !Files.isDirectory(path) && defaultParser.accepts(path);
    }

    /**
     * Creates and returns a new instance of {@link #parserClass}.
     *
     * @return an instance of {@link #parserClass}.
     * @throws InstantiationException if the parser class cannot be instantiated
     * @throws IllegalAccessException if the parser class cannot be accessed
     */
    private CodeParser create() throws IllegalAccessException, InstantiationException {
        return parserClass.newInstance();
    }
}
