package org.qmul.csar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class LoggingCsarErrorListener implements CsarErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCsarErrorListener.class);

    @Override
    public void errorInitializing() {
        LOGGER.error("Found no suitable plugins.");
    }

    @Override
    public void errorParsingCsarQuery(Exception ex) {
        LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
        LOGGER.debug("Parsing Csar Query Error", ex);
    }

    @Override
    public void fatalInitializingParsing(Exception ex) {
        LOGGER.error("Failed to initialize the parser because {}", ex.getMessage());
        LOGGER.debug("Initializing Parsing Error", ex);
    }

    @Override
    public void errorParsing(Path path, Exception ex) {
        String phrase = (ex instanceof IOException) ? "read" : "parse";
        LOGGER.warn("Failed to {} file {} because {}", phrase, path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing Error (Recoverable)", ex);
    }

    @Override
    public void fatalErrorParsing(Path path, Exception ex) {
        LOGGER.error("Parsing terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing Error (Unrecoverable)", ex);
    }

    @Override
    public void errorPostProcessing(Exception ex) {
        LOGGER.error("Post-processing terminated because {}", ex.getMessage());
        LOGGER.debug("Post-processing Error", ex);
    }

    @Override
    public void errorSearching(Path path, Exception ex) {
        LOGGER.warn("Failed to search file {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Searching Error (Recoverable)", ex);
    }

    @Override
    public void fatalErrorSearching(Path path, Exception ex) {
        LOGGER.error("Searching terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Searching Error (Unrecoverable)", ex);
    }
}
