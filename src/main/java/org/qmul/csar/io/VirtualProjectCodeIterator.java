package org.qmul.csar.io;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates over specified code files. You can add files even after partially iterating.
 */
public class VirtualProjectCodeIterator implements PathIterator {

    private final List<Path> files = new ArrayList<>(); // the element collection
    private int cursor = 0; // the index of the next element to return

    public void addFile(Path path) {
        files.add(path);
    }

    @Override
    public boolean hasNext() {
        return cursor < files.size();
    }

    @Override
    public Path next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return files.get(cursor++);
    }
}
