package org.qmul.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public final class FileTextPrinter extends TextPrinter {

    private final Path filePath;

    public FileTextPrinter(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean print(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(text);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
