package org.qmul.csar.output;

import java.util.List;

public abstract class ResultFormatter {

    public abstract String format(List<Result> results) throws Exception;
}
