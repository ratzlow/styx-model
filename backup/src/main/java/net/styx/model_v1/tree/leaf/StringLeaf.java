package net.styx.model_v1.tree.leaf;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Leaf;

import java.util.Objects;


public class StringLeaf extends AbstractLeaf<String> {

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    public StringLeaf(NodeID nodeID) {
        super(nodeID);
    }

    public StringLeaf(NodeID nodeID, String val) {
        super(nodeID, val);
    }

    public StringLeaf(NodeID nodeID, String val, boolean markDirty) {
        super(nodeID, val, markDirty);
    }


    //----------------------------------------------------------------------
    // API
    //----------------------------------------------------------------------

    @Override
    public void setValueString(String val) {
        setValue(val);
    }

    @Override
    public void setValueLeaf(Leaf from) {
        setValueString(from.getValueString());
    }

    @Override
    public String getValueString() {
        return getValue();
    }

    @Override
    protected boolean same(String current, String val) {
        return Objects.equals(current, val);
    }
}
