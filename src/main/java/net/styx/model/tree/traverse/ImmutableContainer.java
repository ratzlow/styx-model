package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class ImmutableContainer extends ImmutableNode<Container> implements Container {

    private final Container immutable;

    public ImmutableContainer(Container node, Collection<Node> immutableChildren) {
        super(node);
        immutable = new DefaultContainer(node.getNodeID(), immutableChildren);
    }

    @Override
    public <T> T getLeafValue(NodeID nodeID, Function<Leaf, T> dispatchGet) {
        return immutable.getLeafValue(nodeID, dispatchGet);
    }

    @Override
    public Container setLeaf(Leaf leaf) {
        throw new UnsupportedOperationException(exceptionMsg());
    }

    @Override
    public Container setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        throw new UnsupportedOperationException(exceptionMsg());
    }

    @Override
    public Leaf getLeaf(NodeID nodeID) {
        return immutable.getLeaf(nodeID);
    }

    @Override
    public Container setContainer(Container container) {
        throw new UnsupportedOperationException(exceptionMsg());
    }

    @Override
    public Container getContainer(NodeID nodeID) {
        return immutable.getContainer(nodeID);
    }

    @Override
    public <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz) {
        return immutable.getContainer(nodeID, clazz);
    }

    @Override
    public <E extends Node> Container setGroup(Group<E> group) {
        throw new UnsupportedOperationException(exceptionMsg());
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID) {
        return immutable.getGroup(nodeID);
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz) {
        return immutable.getGroup(nodeID, elementClazz);
    }

    @Override
    public boolean remove(NodeID nodeID) {
        return prevent();
    }

    @Override
    public Iterator<Node> children() {
        return immutable.children();
    }

    @Override
    public void traverse(TreeWalker treeWalker) {
        immutable.traverse(treeWalker);
    }
}
