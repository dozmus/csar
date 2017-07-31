package org.qmul.output;

import java.util.List;

public final class PlainTextResultFormatter extends ResultFormatter {

    @Override
    public String format(List<Result> results) {
        StringBuilder sb = new StringBuilder();

        for (Result result : results) {
            sb.append(result.getPath().toString()).append(":").append(result.getLineNumber())
                    .append(" ").append(result.getCodeFragment());
        }
        return sb.toString();
    }
}
