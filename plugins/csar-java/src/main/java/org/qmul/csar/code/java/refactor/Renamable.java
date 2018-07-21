package org.qmul.csar.code.java.refactor;

import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;

public interface Renamable {

    Path getPath();

    String getIdentifierName();

    FilePosition getIdentifierFilePosition();
}
