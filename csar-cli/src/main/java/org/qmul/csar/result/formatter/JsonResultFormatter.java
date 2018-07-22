package org.qmul.csar.result.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.qmul.csar.code.Result;

import java.util.Comparator;
import java.util.List;

/**
 * A JSON formatter, this sorts the results by Path and then by line number.
 */
public final class JsonResultFormatter implements ResultFormatter {

    @Override
    public String format(List<Result> results) throws Exception {
        results.sort(Comparator.comparing(Result::getPath).thenComparing(Result::getLineNumber));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
    }

    @Override
    public String toString() {
        return "Json";
    }
}
