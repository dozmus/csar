package org.qmul.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public final class FileTextWriter extends TextPrinter {

    private final Path filePath;

    public FileTextWriter(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean write(String results) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(results);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
