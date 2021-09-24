package net.styx.model.meta;

public class GenericNode<T extends NodeType<?>> implements Node<T> {
    private NodePath<T> path;
    private StateTracker tracker;

    public GenericNode(StateTracker tracker, NodePath<T> path) {
        this.path = path;
        this.tracker = tracker;
    }

    @Override
    public NodePath<T> getNodePath() {
        return path;
    }

    /**
     * @param prefix fqPath of current element in joined tree
     * @param stateTracker of joined tree that is now also hosting this node state
     */
    @Override
    public void connect(NodePath<T> prefix, StateTracker stateTracker) {
        stateTracker.set(prefix, this);
        this.tracker = stateTracker.load(prefix, this.path, tracker);
        this.path = prefix;
    }

    @Override
    public void disconnect() {
        NodePath<T> defaultPath = new NodePath<>(new NodeID<>(path.getLeaf().def()));
        this.tracker = this.tracker.unload(defaultPath, this.path);
        this.path = defaultPath;
    }

    @Override
    public String toString() {
        return path.getLeaf().def().getDefaultName() + '{' + path + '}';
    }

    public StateTracker tracker() {
        return tracker;
    }
}
