package org.qmul.csar.code.java.parse.code.cache;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.lang.Statement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link PhysicalProjectCodeCache}, which does not utilize I/O - this includes no cache
 * expiration checks.
 */
public class DummyPhysicalProjectCodeCache extends PhysicalProjectCodeCache {

    private final Map<Path, byte[]> cache = new HashMap<>();

    public DummyPhysicalProjectCodeCache(Path csarDirectory, Path projectDirectory) {
        super(csarDirectory, projectDirectory);
    }

    @Override
    protected boolean outdated(Path source, Path cacheFile) {
        return !cache.containsKey(cacheFile);
    }

    @Override
    protected void write(Path cacheFile, Statement statement) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Output output = new Output(outputStream)) {
            kryo.writeObject(output, statement);
        } finally {
            cache.put(cacheFile, outputStream.toByteArray());
        }
    }

    @Override
    protected Statement read(Path cacheFile) {
        byte[] bytes = cache.get(cacheFile);

        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readObject(input, CompilationUnitStatement.class);
        }
    }

    @Override
    public boolean cached(Path path) {
        Path cacheFile = sourceFileToCacheFile(path);
        return cache.containsKey(cacheFile);
    }
}
