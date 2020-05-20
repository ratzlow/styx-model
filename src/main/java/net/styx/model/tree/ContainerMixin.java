package net.styx.model.tree;

import net.styx.model.meta.NodeID;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ContainerMixin extends Container {

    Container delegate();

    @Override
    default Container setLeaf(Leaf leaf) {
        return delegate().setLeaf(leaf);
    }

    @Override
    default Container setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        return delegate().setLeaf(nodeID, dispatchSet);
    }

    @Override
    default Leaf getLeaf(NodeID nodeID) {
        return delegate().getLeaf(nodeID);
    }

    @Override
    default <T> T getLeafValue(NodeID nodeID, Function<Leaf, T> dispatchGet) {
        return delegate().getLeafValue(nodeID, dispatchGet);
    }

    @Override
    default Container setContainer(Container container) {
        return delegate().setContainer(container);
    }

    @Override
    default Container getContainer(NodeID nodeID) {
        return delegate().getContainer(nodeID);
    }

    @Override
    default <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz) {
        return delegate().getContainer(nodeID, clazz);
    }

    @Override
    default <E extends Node> Container setGroup(Group<E> group) {
        return delegate().setGroup(group);
    }

    @Override
    default <E extends Node> Group<E> getGroup(NodeID nodeID) {
        return delegate().getGroup(nodeID);
    }

    @Override
    default <E extends Node> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz) {
        return delegate().getGroup(nodeID, elementClazz);
    }

    @Override
    default boolean remove(NodeID nodeID) {
        return delegate().remove(nodeID);
    }

    @Override
    default NodeID getNodeID() {
        return delegate().getNodeID();
    }

    @Override
    default Iterator<Node> children() {
        return delegate().children();
    }

    @Override
    default boolean isChanged() {
        return delegate().isChanged();
    }

    @Override
    default boolean isEmpty() {
        return delegate().isEmpty();
    }

    @Override
    default void commit() {
        delegate().commit();
    }

    @Override
    default void rollback() {
        delegate().rollback();
    }

    @Override
    default void accept(TreeWalker treeWalker) {
        delegate().accept(treeWalker);
    }
}
