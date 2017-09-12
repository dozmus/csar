package org.qmul.csar.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory which names the threads, according to a name format.
 */
public final class NamedThreadFactory implements ThreadFactory {

    private static final String DEFAULT_NAME_FORMAT = "thread-%d";
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private String nameFormat;

    /**
     * Constructs a new NamedThreadFactory with the specified name format.
     * You can use '%d' in the nameFormat which will be replaced with thread number.
     * If nameFormat is <tt>null</tt> then 'thread-%d' is taken as the name format.
     * @param nameFormat the name format to use
     */
    public NamedThreadFactory(String nameFormat) {
        this.nameFormat = (nameFormat == null) ? DEFAULT_NAME_FORMAT : nameFormat;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(nameFormat.replace("%d", "" + (threadNumber.incrementAndGet())));
        return thread;
    }
}
