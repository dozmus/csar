package org.qmul.csar;

import org.pf4j.ExtensionPoint;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.util.List;

public interface CsarPlugin extends ExtensionPoint {

    void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount);

    void postprocess();

    List<Result> search(CsarQuery csarQuery, int threadCount);
}
