package net.styx.model.meta;

public interface Node<E, T extends NodeDef<E>> {
    void connect(NodePath<T> prefix, StateTracker stateTracker);

    void disconnect();

    // TODO: really used?
    NodePath<T> getNodePath();
}
