package org.qmul.csar.code;

import org.qmul.csar.code.java.JavaCodeParser;
import org.qmul.csar.lang.Statement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class CodeParserFactory {

    // TODO un-hardcode this, perhaps use annotations and reflection?

    /**
     * An array of handled code file extensions.
     */
    private static final String[] HANDLED_CODE_FILE_EXTENSIONS = {".java"};

    public static Statement parse(Path file) throws IOException {
        if (Files.isDirectory(file))
            throw new IllegalArgumentException("path must not be a directory");

        if (!Files.exists(file))
            throw new IllegalArgumentException("file must exist");

        if (file.getFileName().toString().endsWith(".java")) {
            return new JavaCodeParser().parse(file);
        }
        throw new IllegalArgumentException("file extension not handled");
    }

    /**
     * Returns if the argument is a code file for which a parser exists.
     *
     * @param path the path to check is accepted
     * @return if the given file has a defined parser
     * @see #HANDLED_CODE_FILE_EXTENSIONS
     */
    public static boolean accepts(Path path) {
        if (Files.isDirectory(path))
            return false;
        String fileName = path.getFileName().toString();
        return Arrays.stream(HANDLED_CODE_FILE_EXTENSIONS).anyMatch(fileName::endsWith);
    }
}
