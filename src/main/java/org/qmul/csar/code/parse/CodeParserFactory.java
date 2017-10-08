package org.qmul.csar.code.parse;

import org.qmul.csar.code.parse.java.JavaCodeParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class CodeParserFactory {

    /**
     * An array of handled code file extensions.
     */
    private static final String[] HANDLED_CODE_FILE_EXTENSIONS = {".java"};

    /**
     * Returns a suitable {@link CodeParser} for the argument, or throws an exception.
     * @param file the file for which a {@link CodeParser} is needed
     * @return a suitable {@link CodeParser} for the argument
     * @throws IllegalArgumentException if the argument is a directory, does not exist, or is not handled.
     */
    public static CodeParser create(Path file) throws IOException {
        if (Files.isDirectory(file))
            throw new IllegalArgumentException("path must not be a directory");

        if (!Files.exists(file))
            throw new IllegalArgumentException("file must exist");

        if (file.getFileName().toString().endsWith(".java")) {
            return new JavaCodeParser();
        }
        throw new IllegalArgumentException("file extension not handled");
    }

    /**
     * Returns <tt>true</tt> if the argument is a code file for which a parser exists.
     *
     * @param path the path to check is accepted
     * @return <tt>true</tt> if the given file has a defined parser
     * @see #HANDLED_CODE_FILE_EXTENSIONS
     */
    public static boolean accepts(Path path) {
        if (Files.isDirectory(path))
            return false;
        String fileName = path.getFileName().toString();
        return Arrays.stream(HANDLED_CODE_FILE_EXTENSIONS).anyMatch(fileName::endsWith);
    }
}
