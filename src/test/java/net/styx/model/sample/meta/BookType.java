package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Book;

public class BookType extends ComponentType<Book> {
    public static final BookType INSTANCE = new BookType();
    public static final NodePath<BookType> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));
    private final NodeID<NodeType<String>> description = new NodeID<>(Dictionary.DESCRIPTION);

    BookType() {
        super(3, "book");
    }

    public NodeID<NodeType<String>> description() {
        return description;
    }

    public Book create(NodePath<BookType> path, StateTracker tracker) {
        return new Book(tracker, path);
    }
}