package net.styx.model.tree;

import java.math.BigDecimal;

// TODO (FRa) : (FRa): every leaf should have a strict immutable sharable default instance
public interface Leaf extends Node, MutationControl {

    default void setValueString(String value) {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }

    default String getValueString() {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }


    default void setValueInt(int value) {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }

    default int getValueInt() {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }


    default void setValueBigDec(BigDecimal value) {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }

    default BigDecimal getValueBigDec() {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }


    default <T extends Enum<T>> void setValueEnum(T value) {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }

    default <T extends Enum<T>> T getValueEnum() {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }


    default void setValueLong(Long value) {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }

    default Long getValueLong() {
        throw new UnsupportedOperationException(getNodeID().getDescriptor().toString());
    }


    // TODO (FRa) : (FRa): can be hidden behind generics?!
    void setValueLeaf(Leaf from);


    @Override
    default void accept(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
        treeWalker.onExit(getNodeID());
    }
}
