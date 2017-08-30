package org.qmul.csar.result;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public final class JsonResultFormatter extends ResultFormatter {

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
