package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;

import java.util.Objects;


public class LongLeaf extends AbstractLeaf<Long> {

    public static final LongLeaf EMPTY_VAL = new LongLeaf();

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    public LongLeaf() {
        super(Descriptor.UNDEF);
    }

    public LongLeaf(NodeID nodeID) {
        super(nodeID);
    }

    public LongLeaf(NodeID nodeID, Long val) {
        super(nodeID);
        setValue(val);
    }

    public LongLeaf(NodeID nodeID, Long val, boolean markDirty) {
        super(nodeID, val, markDirty);
    }

    //----------------------------------------------------------------------
    // API
    //----------------------------------------------------------------------

    @Override
    public void setValueLong(Long val) {
        setValue(val);
    }

    @Override
    public Long getValueLong() {
        return getValue();
    }

    @Override
    protected boolean same(Long current, Long val) {
        return Objects.equals(current, val);
    }

    @Override
    public void setValueLeaf(Leaf from) {
        setValue(from.getValueLong());
    }
}
