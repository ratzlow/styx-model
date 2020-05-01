package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;

import java.util.Objects;


public class StringLeaf extends AbstractLeaf<String> {

    public static final StringLeaf EMPTY_VAL = new StringLeaf();

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    public StringLeaf() {
        super(Descriptor.UNDEF);
    }

    public StringLeaf(NodeID nodeID) {
        super(nodeID);
    }

    public StringLeaf(String val) {
        this(Descriptor.UNDEF, val);
    }

    public StringLeaf(NodeID nodeID, String val) {
        super(nodeID, val);
    }

    public StringLeaf(String val, boolean markDirty) {
        super(Descriptor.UNDEF, val, markDirty);
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
