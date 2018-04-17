package org.qmul.csar;

import java.nio.file.Path;

/**
 * The {@link CsarErrorListener} to use.
 */
public class CliCsarErrorListener extends LoggingCsarErrorListener {

    @Override
    public void errorInitializing() {
        super.errorInitializing();
        System.exit(Main.EXIT_CODE_INITIALIZING);
    }

    @Override
    public void errorParsingCsarQuery(Exception ex) {
        super.errorParsingCsarQuery(ex);
        System.exit(Main.EXIT_CODE_ERROR_PARSING_CSAR_QUERY);
    }

    @Override
    public void fatalInitializingParsing(Exception ex) {
        super.fatalInitializingParsing(ex);
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
    public void errorPostProcessing(Exception ex) {
        super.errorPostProcessing(ex);
        System.exit(Main.EXIT_CODE_ERROR_POSTPROCESSING_CODE);
    }

    @Override
    public void fatalErrorRefactoring(Path path, Exception ex) {
        super.fatalErrorRefactoring(path, ex);
        System.exit(Main.EXIT_CODE_ERROR_REFACTORING_CODE);
    }
}
