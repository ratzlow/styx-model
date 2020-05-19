package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;

// TODO (FRa) : (FRa): add attributes
public class Shoe implements ContainerMixin {

    public static final Descriptor DESCRIPTOR = Descriptor.SHOE;
    private final Container container;

    public Shoe() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Shoe(Container container) {
        this.container = container;
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
