package org.qmul.csar.result;

import org.qmul.csar.util.StringUtils;

import java.util.List;

/**
 * A plain-text formatter of a {@link Result}.
 */
public final class PlainTextResultFormatter implements ResultFormatter {

    @Override
    public String format(List<Result> results) throws Exception {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);
            sb.append(result.getPath().toString()).append(":").append(result.getLineNumber())
                    .append(" ").append(result.getCodeFragment());

            if (i + 1 < results.size())
                sb.append(StringUtils.LINE_SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PlainText";
    }
}
