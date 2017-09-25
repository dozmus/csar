package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProjectCodeParser {

    private final Iterator<Path> it;

    public AbstractProjectCodeParser(Iterator<Path> it) {
        this.it = Objects.requireNonNull(it);
    }

    public abstract Map<Path, Statement> results();

    /**
     * Returns <tt>true</tt> if an error occurred within {@link #results()} which did not result in an exception being
     * thrown.
     * @return <tt>true</tt> if an error occurred within {@link #results()}
     */
    public abstract boolean errorOccurred();

    protected Iterator<Path> getIt() {
        return it;
    }
}
