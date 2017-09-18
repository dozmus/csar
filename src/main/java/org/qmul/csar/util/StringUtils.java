package org.qmul.csar.util;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Miscellaneous utility methods for interacting with <tt>String</tt>.
 */
public final class StringUtils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String indentation(int identation) {
        return IntStream.range(0, identation).mapToObj(i -> "  ").collect(Collectors.joining());
    }

    public static void append(StringBuilder builder, Optional<Boolean> optional, String s) {
        if (optional.isPresent() && optional.get()) {
            builder.append(s);
        }
    }
}
