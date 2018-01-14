package org.qmul.csar.util;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Miscellaneous utility methods for interacting with <tt>String</tt>.
 */
public final class StringUtils {

    public static final String INDENTATION_UNIT = "  ";

    public static String indentation(int indentation) {
        return IntStream.range(0, indentation).mapToObj(i -> INDENTATION_UNIT).collect(Collectors.joining());
    }

    public static void append(StringBuilder builder, Optional<Boolean> optional, String s) {
        if (optional.isPresent() && optional.get()) {
            builder.append(s);
        }
    }

    public static String getFileNameWithoutExtension(Path path) {
        String fileName = path.getFileName().toString();

        if (fileName.contains(".")) {
            int lastDotIdx = fileName.lastIndexOf(".");
            return fileName.substring(0, lastDotIdx);
        }
        return fileName;
    }

    public static int count(String source, String part) {
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
