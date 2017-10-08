package org.qmul.csar.code.search;

import org.qmul.csar.lang.Statement;

import java.util.List;

public abstract class AbstractProjectCodeSearcher {

    public abstract List<Statement> search();

    public abstract boolean errorOccurred();
}
