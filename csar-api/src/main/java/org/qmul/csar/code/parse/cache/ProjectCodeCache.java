package org.qmul.csar.code.parse.cache;

import org.qmul.csar.lang.Statement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A project-wide code cache.
 */
public interface ProjectCodeCache {

    /**
     * Returns if the argument is cached, and up-to-date.
     */
    boolean cached(Path path);

    /**
     * Returns the {@link Statement} corresponding to the argument source file. This does not check if the cached file
     * is up-to-date.
     *
     * @throws FileNotFoundException if the file was not found
     */
    Statement get(Path path) throws IOException, OutdatedCacheException;

    /**
     * Returns if the argument was successfully cached.
     */
    boolean put(Path path, Statement statement) throws IOException;
}
