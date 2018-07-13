package org.qmul.csar.util;

/**
 * A stopwatch utility.
 * This should only be created and used within a single thread.
 */
public class Stopwatch {

    private long time;

    /**
     * Creates a new instance, with the internal timer set to the current time.
     */
    public Stopwatch() {
        reset();
    }

    public void reset() {
        time = System.nanoTime();
    }

    /**
     * Returns the time elapsed in nanoseconds.
     */
    public long elapsedNano() {
        return System.nanoTime() - time;
    }

    /**
     * Returns the time elapsed in milliseconds.
     */
    public long elapsedMillis() {
        return (long) (elapsedNano() / 1E6);
    }
}
