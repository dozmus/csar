package org.qmul.csar.util;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A static class containing different implementations of {@link ToStringStyle}.
 */
public final class ToStringStyles {

    public static final ToStringStyle SHORT_DEFAULT_STYLE = new ShortDefaultToStringStyle();
    public static final ToStringStyle SHORT_MULTI_LINE_STYLE = new ShortMultiLineToStringStyle();

    /**
     * This is not serializable.
     * Code adapted from the apache commons lang3 library.
     */
    private static final class ShortDefaultToStringStyle extends ToStringStyle {

        private ShortDefaultToStringStyle() {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
        }
    }

    /**
     * This is not serializable.
     * Code adapted from the apache commons lang3 library.
     */
    private static final class ShortMultiLineToStringStyle extends ToStringStyle {

        private ShortMultiLineToStringStyle() {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
            setContentStart("[");
            setFieldSeparator(System.lineSeparator() + "  ");
            setFieldSeparatorAtStart(true);
            setContentEnd(System.lineSeparator() + "]");
        }
    }
}
