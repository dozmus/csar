package org.qmul.csar;

import java.nio.file.Path;

public class DefaultCsarErrorListener implements CsarErrorListener {

    private final Csar csar;

    public DefaultCsarErrorListener(Csar csar) {
        this.csar = csar;
    }

    @Override
    public void fatalErrorInitializing() {
        csar.setErrorOccurred();
    }

    @Override
    public void fatalErrorParsingCsarQuery(Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void fatalErrorInitializingParsing(Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void errorParsing(Path path, Exception ex) {
    }

    @Override
    public void fatalErrorParsing(Path path, Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void fatalErrorPostProcessing(Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void errorSearching(Path path, Exception ex) {
    }

    @Override
    public void fatalErrorSearching(Path path, Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void errorRefactoring(Path path, Exception ex) {

    }

    @Override
    public void fatalErrorRefactoring(Path path, Exception ex) {
        csar.setErrorOccurred();
    }
}
