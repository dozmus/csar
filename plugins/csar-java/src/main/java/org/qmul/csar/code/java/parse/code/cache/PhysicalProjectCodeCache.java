package org.qmul.csar.code.java.parse.code.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.parse.cache.OutdatedCacheException;
import org.qmul.csar.code.parse.cache.ProjectCodeCache;
import org.qmul.csar.lang.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

/**
 * A project code cache which saves to the local file system.
 * This requires the argument csarDirectory to exist, or it will fail.
 */
public class PhysicalProjectCodeCache implements ProjectCodeCache {

    private final String csarDirectory;
    private final String projectDirectory;
    protected final Kryo kryo = new Kryo();

    public PhysicalProjectCodeCache(Path csarDirectory, Path projectDirectory) {
        this.csarDirectory = csarDirectory.toAbsolutePath().normalize().toString();
        this.projectDirectory = projectDirectory.toAbsolutePath().normalize().toString();
        KryoConfigurer.register(kryo);
    }

    @Override
    public boolean cached(Path path) {
        Path cacheFile = sourceFileToCacheFile(path);
        return Files.exists(cacheFile);
    }

    @Override
    public Statement get(Path path) throws IOException, OutdatedCacheException {
        if (!cached(path)) {
            throw new FileNotFoundException();
        }

        // Map to cache file
        Path cacheFile = sourceFileToCacheFile(path);

        if (outdated(path, cacheFile)) {
            throw new OutdatedCacheException();
        }

        // Read
        try {
            return read(cacheFile);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean put(Path path, Statement statement) {
        // Create output file
        Path cacheFile = sourceFileToCacheFile(path);

        // Write
        try {
            write(cacheFile, statement);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected boolean outdated(Path source, Path cacheFile) throws IOException {
        FileTime sourceDate = Files.getLastModifiedTime(source);
        FileTime cacheDate = Files.getLastModifiedTime(cacheFile);
        return sourceDate.compareTo(cacheDate) > 0;
    }

    protected void write(Path cacheFile, Statement statement) throws IOException {
        if (!Files.isDirectory(cacheFile.getParent())) {
            Files.createDirectories(cacheFile.getParent());
        }

        try (Output output = new Output(new FileOutputStream(cacheFile.toAbsolutePath().toString()))) {
            kryo.writeObject(output, statement);
        }
    }

    protected Statement read(Path cacheFile) throws IOException {
        try (Input input = new Input(new FileInputStream(cacheFile.toAbsolutePath().toString()))) {
            return kryo.readObject(input, CompilationUnitStatement.class);
        }
    }

    public Path sourceFileToCacheFile(Path path) {
        String file = path.toAbsolutePath().normalize().toString();
        return Paths.get(csarDirectory, file.substring(projectDirectory.length()) + ".bin");
    }
}
