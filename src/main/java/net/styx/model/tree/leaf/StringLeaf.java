package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
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

    public StringLeaf(Descriptor descriptor) {
        super(descriptor);
    }

    public StringLeaf(String val) {
        this(Descriptor.UNDEF, val);
    }

    public StringLeaf(Descriptor descriptor, String val) {
        super(descriptor, val);
    }

    public StringLeaf(String val, boolean markDirty) {
        super(Descriptor.UNDEF, val, markDirty);
    }

    public StringLeaf(Descriptor descriptor, String val, boolean markDirty) {
        super(descriptor, val, markDirty);
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
