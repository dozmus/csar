package org.qmul.csar.io;

import java.nio.file.Path;
import java.util.Iterator;

public interface PathIterator extends Iterator<Path> {

    @Override
    boolean hasNext();

    @Override
    Path next();
}
