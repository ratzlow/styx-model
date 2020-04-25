package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
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

    public LongLeaf(Descriptor descriptor) {
        super(descriptor);
    }

    public LongLeaf(Descriptor descriptor, Long val) {
        super(descriptor);
        setValue(val);
    }

    public LongLeaf(Descriptor descriptor, Long val, boolean markDirty, boolean markFrozen) {
        super(descriptor, val, markDirty, markFrozen);
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
