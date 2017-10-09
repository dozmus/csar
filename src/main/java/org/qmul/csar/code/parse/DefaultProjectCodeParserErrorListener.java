package org.qmul.csar.code.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultProjectCodeParserErrorListener implements ProjectCodeParserErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProjectCodeParserErrorListener.class);

    @Override
    public void parsingError(Path path, Exception ex) {
        String phrase = (ex instanceof IOException) ? "read" : "parse";
        LOGGER.error("Failed to {} file {} because {}", phrase, path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("ParsingError", ex);
    }

    @Override
    public void unknownError(Path path, Exception ex) {
        LOGGER.error("Parsing terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("UnknownError", ex);
    }
}
