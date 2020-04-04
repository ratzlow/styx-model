package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;

import java.util.Objects;


public class LongLeaf extends AbstractLeaf<Long> {

    public static final LongLeaf EMPTY_VAL = new LongLeaf();

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    public LongLeaf() {
        super(Descriptor.UNDEF);
    }

    public LongLeaf(Descriptor descriptor) {
        super(descriptor);
    }

    public LongLeaf(Descriptor descriptor, Long val) {
        super(descriptor);
        setValue(val);
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
}
