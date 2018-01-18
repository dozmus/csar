package org.qmul.csar.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultProjectCodeErrorListener implements ProjectCodeErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProjectCodeErrorListener.class);

    @Override
    public void reportRecoverableError(Path path, Exception ex) {
        String phrase = (ex instanceof IOException) ? "read" : "parse";
        LOGGER.warn("Failed to {} file {} because {}", phrase, path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing Error (Recoverable)", ex);
    }

    @Override
    public void reportUnrecoverableError(Path path, Exception ex) {
        LOGGER.error("Parsing terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing Error (Unrecoverable)", ex);
    }
}
