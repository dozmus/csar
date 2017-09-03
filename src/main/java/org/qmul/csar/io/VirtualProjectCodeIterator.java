package org.qmul.csar.io;

import java.nio.file.Path;

/**
 * Iterates over specified code files. You can add files even after partially iterating.
 */
public class VirtualProjectCodeIterator extends ProjectCodeIterator {

    public VirtualProjectCodeIterator() {
        super(null);
    }

    @Override
    public void init() {
        throw new IllegalStateException("cannot initialize a VirtualProjectCodeIterator");
    }

    @Override
    public void addFile(Path path) {
        super.addFile(path);
    }
}
