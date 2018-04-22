package org.qmul.csar;

import java.nio.file.Path;

/**
 * The {@link CsarErrorListener} to use.
 */
public class CliCsarErrorListener extends LoggingCsarErrorListener {

    @Override
    public void fatalErrorInitializing() {
        super.fatalErrorInitializing();
        System.exit(Main.EXIT_CODE_INITIALIZING);
    }

    @Override
    public void fatalErrorParsingCsarQuery(Exception ex) {
        super.fatalErrorParsingCsarQuery(ex);
        System.exit(Main.EXIT_CODE_ERROR_PARSING_CSAR_QUERY);
    }

    @Override
    public void fatalErrorInitializingParsing(Exception ex) {
        super.fatalErrorInitializingParsing(ex);
        System.exit(Main.EXIT_CODE_ERROR_PARSING_CODE);
    }

    @Override
    public void fatalErrorParsing(Path path, Exception ex) {
        super.fatalErrorParsing(path, ex);
        System.exit(Main.EXIT_CODE_ERROR_PARSING_CODE);
    }

    @Override
    public void fatalErrorSearching(Path path, Exception ex) {
        super.fatalErrorSearching(path, ex);
        System.exit(Main.EXIT_CODE_ERROR_SEARCHING_CODE);
    }

    @Override
    public void fatalErrorPostProcessing(Exception ex) {
        super.fatalErrorPostProcessing(ex);
        System.exit(Main.EXIT_CODE_ERROR_POSTPROCESSING_CODE);
    }

    @Override
    public void fatalErrorRefactoring(Path path, Exception ex) {
        super.fatalErrorRefactoring(path, ex);
        System.exit(Main.EXIT_CODE_ERROR_REFACTORING_CODE);
    }
}
