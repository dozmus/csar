package org.qmul.csar.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileHelper {

    public static void write(Path path, String text) throws IOException {
        Files.write(path, text.getBytes());
    }
}
