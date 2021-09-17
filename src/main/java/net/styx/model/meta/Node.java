package net.styx.model.meta;

/**
 *
 * @param <E> the domain entity representing this node
 * @param <T> the domain entity's definition (an entity could have multiple ones in theory)
 */
public interface Node<E, T extends NodeDef<E>> {
    void connect(NodePath<T> prefix, StateTracker stateTracker);

    void disconnect();

    // TODO: really used?
    NodePath<T> getNodePath();
}
