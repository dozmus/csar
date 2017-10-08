package org.qmul.csar.code.search;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class AbstractProjectCodeSearcher { // TODO document

    public abstract List<Statement> search(CsarQuery query, Map<Path, Statement> code);

    public abstract boolean errorOccurred();
}
