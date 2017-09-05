package org.qmul.csar.util;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import org.qmul.csar.result.JsonResultFormatter;
import org.qmul.csar.result.PlainTextResultFormatter;
import org.qmul.csar.result.ResultFormatter;

/**
 * A command-line parser for fields of type {@link ResultFormatter}.
 */
public final class ResultFormatterConverter implements IStringConverter<ResultFormatter> {

    @Override
    public ResultFormatter convert(String value) {
        switch (value.toLowerCase().trim()) {
            case "json":
                return new JsonResultFormatter();
            case "plain text":
            case "plaintext":
            case "plain":
            case "text":
            case "txt":
                return new PlainTextResultFormatter();
            default:
                throw new ParameterException("invalid output format specified");
        }
    }
}
