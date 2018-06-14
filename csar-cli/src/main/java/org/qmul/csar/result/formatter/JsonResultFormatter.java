package org.qmul.csar.result.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.qmul.csar.code.Result;

import java.util.List;

/**
 * A JSON formatter.
 */
public final class JsonResultFormatter implements ResultFormatter {

    @Override
    public String format(List<Result> results) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
    }

    @Override
    public String toString() {
        return "Json";
    }
}
