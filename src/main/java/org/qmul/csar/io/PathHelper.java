package org.qmul.csar.io;

import org.qmul.csar.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A set of utility methods concerning {@link Path}.
 */
public final class PathHelper {

    /**
     * Writes the argument to the given <tt>Path</tt> argument.
     *
     * @param path the path to the file
     * @param text the text to write to the file
     * @throws IOException if an I/O error occurs
     */
    public static void write(Path path, String text) throws IOException {
        Files.write(path, text.getBytes());
    }

    /**
     * Returns the contents of the argument.
     *
     * @param path the path to the file
     * @return the contents of the argument
     * @throws IOException if an I/O error occurs
     */
    public static String read(Path path) throws IOException {
        return String.join(StringUtils.LINE_SEPARATOR, Files.readAllLines(path));
    }
}
