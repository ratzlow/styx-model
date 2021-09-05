package net.styx.model_v1.tree;

import net.styx.model_v1.meta.NodeID;

// TODO (FRa) : (FRa): add cloning, pruning empty/unset nodes
// TODO (FRa) : (FRa): rename as it is not by design a tree walker anymore
public interface TreeWalker {

    void onEnter(Leaf leaf);

    void onEnter(Container container);

    void onEnter(Group<?> group);

    void onExit(NodeID nodeID);

    default boolean proceed() {
        return true;
    }
}
