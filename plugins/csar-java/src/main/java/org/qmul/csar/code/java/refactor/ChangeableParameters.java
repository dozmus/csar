package org.qmul.csar.code.java.refactor;

import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.util.List;

public interface ChangeableParameters {

    FilePosition getLeftParenthesisPosition();

    FilePosition getRightParenthesisPosition();

    List<FilePosition> getCommaFilePositions();

    int getLineNumber();

    Path getPath();
}
