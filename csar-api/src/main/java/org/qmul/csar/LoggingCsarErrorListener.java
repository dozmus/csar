package org.qmul.csar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class LoggingCsarErrorListener implements CsarErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCsarErrorListener.class);

    @Override
    public void fatalErrorInitializing() {
        LOGGER.error("Failed to start csar because found no suitable plugins.");
        LOGGER.error("Initializing Csar error (Unrecoverable)");
    }

    @Override
    public void fatalErrorParsingCsarQuery(Exception ex) {
        LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
        LOGGER.debug("Parsing Csar Query error (Unrecoverable)", ex);
    }

    @Override
    public void fatalErrorInitializingParsing(Exception ex) {
        LOGGER.error("Failed to initialize the parser because {}", ex.getMessage());
        LOGGER.debug("Initializing Parsing error (Unrecoverable)", ex);
    }

    @Override
    public void errorParsing(Path path, Exception ex) {
        String phrase = (ex instanceof IOException) ? "read" : "parse";
        LOGGER.warn("Failed to {} file {} because {}", phrase, path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing error (Recoverable)", ex);
    }

    @Override
    public void fatalErrorParsing(Path path, Exception ex) {
        LOGGER.error("Parsing terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Parsing error (Unrecoverable)", ex);
    }

    @Override
    public void fatalErrorPostProcessing(Exception ex) {
        LOGGER.error("Post-processing terminated because {}", ex.getMessage());
        LOGGER.debug("Post-processing error (Unrecoverable)", ex);
    }

    @Override
    public void errorSearching(Path path, Exception ex) {
        LOGGER.warn("Failed to search file {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Searching error (Recoverable)", ex);
    }

    @Override
    public void fatalErrorSearching(Path path, Exception ex) {
        LOGGER.error("Searching terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Searching error (Unrecoverable)", ex);
    }

    @Override
    public void errorRefactoring(Path path, Exception ex) {
        LOGGER.warn("Failed to refactor file {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Refactoring error (Recoverable)", ex);
    }

    @Override
    public void fatalErrorRefactoring(Path path, Exception ex) {
        LOGGER.error("Refactoring terminated {} because {}", path.getFileName().toString(), ex.getMessage());
        LOGGER.debug("Refactoring error (Unrecoverable)", ex);
    }
}
