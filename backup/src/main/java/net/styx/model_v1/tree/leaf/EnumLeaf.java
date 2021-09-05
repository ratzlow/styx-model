package net.styx.model_v1.tree.leaf;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Leaf;

public class EnumLeaf extends AbstractLeaf<Enum<?>> {

    public EnumLeaf(NodeID nodeID) {
        super(nodeID);
    }

    @Override
    public <T extends Enum<T>> void setValueEnum(T val) {
        setValue(val);
    }

    @Override
    public <T extends Enum<T>> T getValueEnum() {
        return (T) getValue();
    }

    @Override
    protected boolean same(Enum<?> current, Enum<?> val) {
        return current == val;
    }

    @Override
    public void setValueLeaf(Leaf from) {
        setValue(from.getValueEnum());
    }
}
