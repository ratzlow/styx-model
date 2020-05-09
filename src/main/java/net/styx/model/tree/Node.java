package net.styx.model.tree;

import net.styx.model.meta.NodeID;

public interface Node extends Stateful, Traversable {
    NodeID getNodeID();
}
