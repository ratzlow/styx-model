package net.styx.model.tree;

import net.styx.model.meta.NodeID;

// TODO (FRa) : (FRa): adding onChange hook?
public interface Node extends Stateful, Traversable {
    NodeID getNodeID();
}
