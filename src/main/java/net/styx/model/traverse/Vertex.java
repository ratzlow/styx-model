package net.styx.model.traverse;

import net.styx.model.tree.Described;

public interface Vertex extends Described {

    Vertex getChild(Step child);

    Vertex getChild(Step child, NodeFactory nodeFactory);

    void removeChild(Step child);
}