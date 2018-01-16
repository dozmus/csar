package org.qmul.csar.util;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import org.qmul.csar.result.formatter.JsonResultFormatter;
import org.qmul.csar.result.formatter.PlainTextResultFormatter;
import org.qmul.csar.result.formatter.ResultFormatter;

/**
 * A String argument to {@link ResultFormatter} converter for command-line parsing.
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
