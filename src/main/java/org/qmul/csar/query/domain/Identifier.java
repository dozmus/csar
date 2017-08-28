package org.qmul.csar.query.domain;

import java.util.Objects;

public class Identifier {

    private String name;
    private String type;

    public Identifier(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }
}
