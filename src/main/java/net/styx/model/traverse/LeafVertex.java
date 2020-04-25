package net.styx.model.traverse;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Leaf;

public class LeafVertex implements Vertex {

    private final Leaf parent;

    public LeafVertex(Leaf existing) {
        this.parent = existing;
    }


    @Override
    public Vertex getChild(Step child) {
        throw new UnsupportedOperationException("No getChild from Leaf as parent possible!");
    }

    @Override
    public Vertex getChild(Step child, NodeFactory nodeFactory) {
        throw new UnsupportedOperationException("No getChild from Leaf as parent possible!");
    }

    @Override
    public void removeChild(Step child) {
        throw new UnsupportedOperationException("No getChild from Leaf as parent possible!");
    }

    @Override
    public Descriptor getDescriptor() {
        return parent.getDescriptor();
    }
}
