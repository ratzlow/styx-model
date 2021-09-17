package net.styx.model.sample;

import net.styx.model.meta.Node;
import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.sample.meta.BookDef;

public class Book implements Node<Book, BookDef> {
    private static final BookDef DEF = BookDef.INSTANCE;
    private NodePath<BookDef> path;
    private StateTracker tracker;

    public Book(StateTracker stateTracker, NodePath<BookDef> path) {
        this.path = path;
        this.tracker = stateTracker;
    }

    public Book(StateTracker stateTracker) {
        this.tracker = stateTracker;
        this.path = BookDef.DEFAULT_PATH;
    }

    public Book() {
        path = BookDef.DEFAULT_PATH;
        tracker = new StateTracker();
    }

    public String getDescription() {
        return tracker.get(path, DEF.description());
    }

    public void setDescription(String description) {
        tracker.set(path, DEF.description(), description);
    }

    @Override
    public NodePath<BookDef> getNodePath() {
        return path;
    }

    /**
     * @param prefix fqPath of current element in joined tree
     * @param stateTracker of joined tree that is now also hosting this node state
     */
    @Override
    public void connect(NodePath<BookDef> prefix, StateTracker stateTracker) {
        stateTracker.set(prefix, this);
        this.tracker = stateTracker.load(prefix, this.path, tracker);
        this.path = prefix;
    }

    @Override
    public void disconnect() {
        this.tracker = this.tracker.unload(BookDef.DEFAULT_PATH, this.path);
        this.path = BookDef.DEFAULT_PATH;
    }

    @Override
    public String toString() {
        return "Book{path='" + path + '}';
    }
}
