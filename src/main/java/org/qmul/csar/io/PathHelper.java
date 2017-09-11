package org.qmul.csar.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A set of utility methods concerning {@link Path}.
 */
public final class PathHelper {

    /**
     * Writes the <tt>String</tt> argument to the given <tt>Path</tt> argument.
     *
     * @param path the path to the file
     * @param text the text to write to the file
     * @throws IOException if an I/O error occurs
     */
    public static void write(Path path, String text) throws IOException {
        Files.write(path, text.getBytes());
    }
}