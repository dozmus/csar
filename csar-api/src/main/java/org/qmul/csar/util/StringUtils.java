package org.qmul.csar.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Miscellaneous utility methods for interacting with <tt>String</tt>.
 */
public final class StringUtils {

    /**
     * One unit of indentation, as used in {@link #indentation(int)}.
     */
    public static final String INDENTATION_UNIT = "  ";

    /**
     * Returns the argument many indentations.
     *
     * @see #INDENTATION_UNIT
     */
    public static String indentation(int indentation) {
        return IntStream.range(0, indentation).mapToObj(i -> INDENTATION_UNIT).collect(Collectors.joining());
    }

    /**
     * Appends the argument String to the argument builder if the argument Optional is present and <tt>true</tt>.
     */
    public static void append(StringBuilder builder, Optional<Boolean> optional, String s) {
        if (optional.isPresent() && optional.get()) {
            builder.append(s);
        }
    }

    /**
     * Returns the argument file's name without its extension.
     *
     * @throws IllegalArgumentException if the argument path is a directory
     */
    public static String fileNameWithoutExtension(Path path) throws IllegalArgumentException {
        if (Files.isDirectory(path))
            throw new IllegalArgumentException("invalid argument it must not be a directory");

        String fileName = path.getFileName().toString();

        if (fileName.contains(".")) {
            int lastDotIdx = fileName.lastIndexOf(".");
            return fileName.substring(0, lastDotIdx);
        }
        return fileName;
    }

    /**
     * Returns how many times the argument part appears in the argument source (case-sensitive).
     * If the argument part is the empty String it returns <tt>0</tt>.
     */
    public static int count(String source, String part) {
        if (part.equals(""))
            return 0;

        // Source: https://stackoverflow.com/a/767910
        int lastIdx = 0;
        int count = 0;

        while (lastIdx != -1) {
            lastIdx = source.indexOf(part, lastIdx);

            if (lastIdx != -1) {
                count++;
                lastIdx += part.length();
            }
        }
        return count;
    }
}
