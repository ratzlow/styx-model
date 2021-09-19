package net.styx.model.sample;

import net.styx.model.meta.*;

public class Book implements Node<Book.Type> {
    private final NodeMixin<Book.Type> mixin;

    public Book(StateTracker stateTracker, NodePath<Type> path) {
        this.mixin = new NodeMixin<>(stateTracker, path);
    }

    public Book(StateTracker stateTracker) {
        this(stateTracker, Type.DEFAULT_PATH);
    }

    public Book() {
        this(new StateTracker(), Type.DEFAULT_PATH);
    }

    public String getDescription() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.description);
    }

    public void setDescription(String description) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.description, description);
    }

    //------------------------------------------- NodeMixin API --------------------------------------------------------

    @Override
    public NodePath<Type> getNodePath() {
        return mixin.getNodePath();
    }

    @Override
    public void connect(NodePath<Type> prefix, StateTracker stateTracker) {
        mixin.connect(prefix, stateTracker);
    }

    @Override
    public void disconnect() {
        mixin.disconnect();
    }

    @Override
    public String toString() {
        return mixin.toString();
    }

    //------------------------------------------- Meta -----------------------------------------------------------------

    public static class Type extends ComponentType<Book> {
        public static final Type INSTANCE = new Type();
        public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));
        private final NodeID<NodeType<String>> description = new NodeID<>(Dictionary.DESCRIPTION);

        Type() {
            super(3, "book");
        }
    }
}
