package net.styx.model_v1.sample;

import net.styx.model_v1.tree.ContainerMixin;
import net.styx.model_v1.tree.Container;
import net.styx.model_v1.tree.DefaultContainer;
import net.styx.model_v1.tree.Leaf;

public class Shoe implements ContainerMixin {

    public static final SampleDescriptor DESCRIPTOR = SampleDescriptor.SHOE;
    private final Container container;

    public Shoe() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Shoe(Container container) {
        this.container = container;
    }

    //-------------------------------------------------------------------------------------------------
    // public API
    //-------------------------------------------------------------------------------------------------

    public void setColor(Color color) {
        setLeaf(DESCRIPTOR.COLOR, leaf -> leaf.setValueEnum(color));
    }

    public Color getColor() {
        return getLeafValue(DESCRIPTOR.COLOR, Leaf::getValueEnum);
    }

    public void setSize(int size) {
        setLeaf(DESCRIPTOR.SIZE, leaf -> leaf.setValueInt(size));
    }

    public int getSize() {
        return getLeafValue(DESCRIPTOR.SIZE, Leaf::getValueInt);
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
