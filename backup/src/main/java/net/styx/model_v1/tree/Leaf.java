package net.styx.model_v1.tree;

import net.styx.model_v1.meta.NodeID;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;

public interface Leaf extends StatefulNode {

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
    }

    @Override
    default Iterator<StatefulNode> children() {
        return Collections.emptyIterator();
    }

    /**
     * @param childNodeID in dictionary that identifies node delete
     * @return false ... always, since Leafs have no children, this operation will not
     *                   trigger any effect.
     */
    @Override
    default boolean remove(NodeID childNodeID) { return false; }
}
