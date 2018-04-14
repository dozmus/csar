package org.qmul.csar;

import java.nio.file.Path;

public class DefaultCsarErrorListener implements CsarErrorListener {

    private Csar csar;

    public DefaultCsarErrorListener(Csar csar) {
        this.csar = csar;
    }

    @Override
    public void errorInitializing() {
        csar.setErrorOccurred();
    }

    @Override
    public void errorParsingCsarQuery(Exception ex) {
        csar.setErrorOccurred();
    }

    @Override
    public void fatalInitializingParsing(Exception ex) {
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
    public void errorPostProcessing(Exception ex) {
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
