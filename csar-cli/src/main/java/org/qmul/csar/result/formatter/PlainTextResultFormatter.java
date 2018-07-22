package org.qmul.csar.result.formatter;

import org.qmul.csar.code.Result;

import java.util.Comparator;
import java.util.List;

/**
 * A plain-text formatter, this sorts the results by Path and then by line number.
 */
public final class PlainTextResultFormatter implements ResultFormatter {

    @Override
    public String format(List<Result> results) {
        results.sort(Comparator.comparing(Result::getPath).thenComparing(Result::getLineNumber));

        if (results.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                sb.append(result.getPath().toString()).append(":").append(result.getLineNumber())
                        .append(" ").append(result.getCodeFragment());

                if (i + 1 < results.size())
                    sb.append(System.lineSeparator());
            }
            return sb.toString();
        } else {
            return "None";
        }
    }

    @Override
    public String toString() {
        return "PlainText";
    }
}
