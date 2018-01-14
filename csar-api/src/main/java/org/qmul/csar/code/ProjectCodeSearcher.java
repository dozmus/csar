package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ProjectCodeSearcher {

    List<Result> results();

    void setCsarQuery(CsarQuery csarQuery);

    void setIterator(Iterator<Map.Entry<Path, Statement>> iterator);

    boolean errorOccurred();
}
