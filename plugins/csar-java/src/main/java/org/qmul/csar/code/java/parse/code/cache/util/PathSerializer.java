package org.qmul.csar.code.java.parse.code.cache.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathSerializer extends FieldSerializer<Path> {

    // Source: https://stackoverflow.com/q/21076136

    public PathSerializer(Kryo kryo) {
        super(kryo, Path.class);
    }

    public void write(Kryo kryo, Output output, Path path) {
        kryo.writeObject(output, path.toString());
    }

    public Path read(Kryo kryo, Input input, Class<Path> type) {
        return Paths.get(kryo.readObject(input, String.class));
    }
}
