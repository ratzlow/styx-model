package net.styx.model.sample;

import net.styx.model.meta.*;
import net.styx.model.sample.meta.Dictionary;

public class Book implements Node<Book.Type> {
    private NodePath<Type> path;
    private StateTracker tracker;

    public Book(StateTracker stateTracker, NodePath<Type> path) {
        this.path = path;
        this.tracker = stateTracker;
    }

    public Book(StateTracker stateTracker) {
        this.tracker = stateTracker;
        this.path = Type.DEFAULT_PATH;
    }

    public Book() {
        path = Type.DEFAULT_PATH;
        tracker = new StateTracker();
    }

    public String getDescription() {
        return tracker.get(path, Type.INSTANCE.description);
    }

    public void setDescription(String description) {
        tracker.set(path, Type.INSTANCE.description, description);
    }

    @Override
    public NodePath<Type> getNodePath() {
        return path;
    }

    /**
     * @param prefix fqPath of current element in joined tree
     * @param stateTracker of joined tree that is now also hosting this node state
     */
    @Override
    public void connect(NodePath<Type> prefix, StateTracker stateTracker) {
        stateTracker.set(prefix, this);
        this.tracker = stateTracker.load(prefix, this.path, tracker);
        this.path = prefix;
    }

    @Override
    public void disconnect() {
        this.tracker = this.tracker.unload(Type.DEFAULT_PATH, this.path);
        this.path = Type.DEFAULT_PATH;
    }

    @Override
    public String toString() {
        return "Book{path='" + path + '}';
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class Type extends ComponentType<Book> {
        public static final Type INSTANCE = new Type();
        public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));
        private final NodeID<NodeType<String>> description = new NodeID<>(Dictionary.DESCRIPTION);

        Type() {
            super(3, "book");
        }
    }
}
