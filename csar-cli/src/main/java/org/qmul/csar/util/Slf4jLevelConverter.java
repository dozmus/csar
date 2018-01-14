package org.qmul.csar.util;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import org.slf4j.event.Level;

public final class Slf4jLevelConverter implements IStringConverter<Level> {

    @Override
    public Level convert(String value) {
        for (Level level : Level.values()) {
            if (level.toString().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new ParameterException("invalid log level specified");
    }
}
