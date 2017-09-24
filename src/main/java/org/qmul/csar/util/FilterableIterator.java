package org.qmul.csar.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilterableIterator<T> implements Iterator<T> {

    private final Iterator<T> it;
    private final Predicate<T> filter;
    private T nextElement = null;

    public FilterableIterator(Iterator<T> it, Predicate<T> filter) {
        this.it = it;
        this.filter = filter;
        findNextElement();
    }

    private void findNextElement() {
        while (it.hasNext()) {
            T next = it.next();

            if (filter.test(next)) {
                nextElement = next;
                return;
            }
        }
        nextElement = null;
    }

    @Override
    public boolean hasNext() {
        return nextElement != null;
    }

    @Override
    public T next() {
        if (hasNext()) {
            T element = nextElement;
            findNextElement();
            return element;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        it.forEachRemaining(action);
    }
}
