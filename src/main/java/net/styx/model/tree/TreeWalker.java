package net.styx.model.tree;

import net.styx.model.meta.NodeID;

public interface TreeWalker {

    void onEnter(Leaf leaf);

    void onEnter(Container container);

    void onEnter(Group<?> group);

    void onExit(NodeID nodeID);
}
