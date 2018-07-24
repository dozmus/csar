package org.qmul.csar.code.parse.cache;

import org.qmul.csar.lang.Statement;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * A NOP project code cache.
 */
public class DummyProjectCodeCache implements ProjectCodeCache {

    @Override
    public boolean cached(Path path) {
        return false;
    }

    @Override
    public Statement get(Path path) throws FileNotFoundException {
        throw new FileNotFoundException(path.toAbsolutePath().toString());
    }

    @Override
    public boolean put(Path path, Statement statement) {
        return false;
    }
}
