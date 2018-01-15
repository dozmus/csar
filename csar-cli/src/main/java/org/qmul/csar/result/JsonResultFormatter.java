package org.qmul.csar.result;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * A JSON formatter of a {@link Result}.
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