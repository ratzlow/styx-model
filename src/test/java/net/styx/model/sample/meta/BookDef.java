package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Book;

public class BookDef extends ComponentDef<Book> {
    public static final BookDef INSTANCE = new BookDef();
    public static final NodePath<BookDef> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));
    private final NodeID<NodeDef<String>> description = new NodeID<>(Dictionary.DESCRIPTION);

    BookDef() {
        super(3, "book");
    }

    public NodeID<NodeDef<String>> description() {
        return description;
    }

    public Book create(NodePath<BookDef> path, StateTracker tracker) {
        return new Book(tracker, path);
    }
}