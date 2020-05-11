package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.function.Consumer;

public class ImmutableContainer extends ImmutableNode<Container> implements Container {

    public ImmutableContainer(Container node) {
        super(node);
    }

    @Override
    public void setLeaf(Leaf leaf) {
        prevent();
    }

    @Override
    public void setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        prevent();
    }

    @Override
    public Leaf getLeaf(NodeID nodeID) {
        return node.getLeaf(nodeID);
    }

    @Override
    public void setContainer(Container container) {
        prevent();
    }

    @Override
    public Container getContainer(NodeID nodeID) {
        return node.getContainer(nodeID);
    }

    @Override
    public <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz) {
        return node.getContainer(nodeID, clazz);
    }

    @Override
    public <E extends Node> void setGroup(Group<E> group) {
        prevent();
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID) {
        return node.getGroup(nodeID);
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz) {
        return node.getGroup(nodeID, elementClazz);
    }

    @Override
    public boolean remove(NodeID nodeID) {
        return prevent();
    }
}
