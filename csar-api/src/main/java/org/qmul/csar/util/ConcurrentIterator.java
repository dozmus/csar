package org.qmul.csar.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A thread-safe wrapper for {@link Iterator}.
 *
 * @param <T>
 */
public class ConcurrentIterator<T> implements Iterator<T> {

    /**
     * The underlying {@link Iterator}.
     */
    private final Iterator<T> it;

    /**
     * Creates a new {@link ConcurrentIterator} wrapping the argument iterator.
     *
     * @param it the iterator to wrap
     */
    public ConcurrentIterator(Iterator<T> it) {
        this.it = it;
    }

    /**
     * Thread-safe.
     * @return {@link #it#hasNext()}
     */
    @Override
    public boolean hasNext() {
        synchronized (it) {
            return it.hasNext();
        }
    }

    /**
     * Thread-safe.
     * @return {@link #it#next()}
     */
    @Override
    public T next() {
        synchronized (it) {
            if (it.hasNext()) {
                return it.next();
            }
        }
        throw new NoSuchElementException("no more files left");
    }
}
