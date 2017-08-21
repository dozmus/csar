package org.qmul.csar.util;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory which names the threads, according to a name format.
 */
public final class NamedThreadFactory implements ThreadFactory {

    private static final String DEFAULT_NAME_FORMAT = "thread-%d";
    private int threadCount = 1;
    private String nameFormat;

    /**
     * Sets the thread name format, %d is replaced with thread number.
     * If nameFormat is null then 'thread-%d' is taken as the name format.
     */
    public NamedThreadFactory(String nameFormat) {
        this.nameFormat = nameFormat == null ? DEFAULT_NAME_FORMAT : nameFormat;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(nameFormat.replace("%d", "" + (threadCount++)));
        return thread;
    }
}
