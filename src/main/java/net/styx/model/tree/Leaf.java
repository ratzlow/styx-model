package net.styx.model.tree;

import java.math.BigDecimal;

public interface Leaf extends Described, Stateful, MutationControl {

    default void setValueString(String value) {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    default String getValueString() {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }


    default void setValueInt(int value) {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    default int getValueInt() {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }


    default void setValueBigDec(BigDecimal value) {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    default BigDecimal getValueBigDec() {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }


    default <T extends Enum<T>> void setValueEnum(T value) {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    default <T extends Enum<T>> T getValueEnum() {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }


    default void setValueLong(Long value) {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    default Long getValueLong() {
        throw new UnsupportedOperationException(getDescriptor().toString());
    }

    // TODO (FRa) : (FRa): can be hidden behind generics
    void setValueLeaf(Leaf from);
}
