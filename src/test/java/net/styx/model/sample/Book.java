package net.styx.model.sample;

import net.styx.model.meta.Node;
import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.sample.meta.BookType;

public class Book implements Node<Book, BookType> {
    private static final BookType DEF = BookType.INSTANCE;
    private NodePath<BookType> path;
    private StateTracker tracker;

    public Book(StateTracker stateTracker, NodePath<BookType> path) {
        this.path = path;
        this.tracker = stateTracker;
    }

    public Book(StateTracker stateTracker) {
        this.tracker = stateTracker;
        this.path = BookType.DEFAULT_PATH;
    }

    public Book() {
        path = BookType.DEFAULT_PATH;
        tracker = new StateTracker();
    }

    public String getDescription() {
        return tracker.get(path, DEF.description());
    }

    public void setDescription(String description) {
        tracker.set(path, DEF.description(), description);
    }

    @Override
    public NodePath<BookType> getNodePath() {
        return path;
    }

    /**
     * @param prefix fqPath of current element in joined tree
     * @param stateTracker of joined tree that is now also hosting this node state
     */
    @Override
    public void connect(NodePath<BookType> prefix, StateTracker stateTracker) {
        stateTracker.set(prefix, this);
        this.tracker = stateTracker.load(prefix, this.path, tracker);
        this.path = prefix;
    }

    @Override
    public void disconnect() {
        this.tracker = this.tracker.unload(BookType.DEFAULT_PATH, this.path);
        this.path = BookType.DEFAULT_PATH;
    }

    @Override
    public String toString() {
        return "Book{path='" + path + '}';
    }
}
