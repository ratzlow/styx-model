package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Leaf;

public class EnumLeaf extends AbstractLeaf<Enum<?>> {

    public static final Leaf EMPTY_VAL = new EnumLeaf(Descriptor.UNDEF);

    public EnumLeaf(Descriptor descriptor) {
        super(descriptor);
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
}
