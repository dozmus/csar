package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.util.OptionalUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractVariableDescriptor implements Descriptor {

    private final IdentifierName identifierName;
    private final Optional<String> identifierType;
    private final Optional<Boolean> finalModifier;

    public AbstractVariableDescriptor(IdentifierName identifierName, Optional<String> identifierType,
            Optional<Boolean> finalModifier) {
        this.identifierName = identifierName;
        this.identifierType = identifierType;
        this.finalModifier = finalModifier;
    }

    public IdentifierName getIdentifierName() {
        return identifierName;
    }

    public Optional<String> getIdentifierType() {
        return identifierType;
    }

    public Optional<Boolean> getFinalModifier() {
        return finalModifier;
    }

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractVariableDescriptor that = (AbstractVariableDescriptor) o;
        return identifierName.nameEquals(that.identifierName)
                && OptionalUtils.lenientEquals(identifierType, that.identifierType)
                && OptionalUtils.lenientEquals(finalModifier, that.finalModifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractVariableDescriptor that = (AbstractVariableDescriptor) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(identifierType, that.identifierType)
                && Objects.equals(finalModifier, that.finalModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, identifierType, finalModifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("identifierName", identifierName)
                .append("identifierType", identifierType)
                .append("finalModifier", finalModifier)
                .toString();
    }
}
