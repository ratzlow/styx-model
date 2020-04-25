package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.leaf.LongLeaf;

public class Book extends IdentifiableDataContainer<Long> {

    public static final Descriptor DESCRIPTOR = Descriptor.BOOK;

    public Book() {
        this(generateUuidLong());
    }

    public Book(Long id) {
        super(DESCRIPTOR, new LongLeaf(DESCRIPTOR.getIDKey().orElseThrow(), id, true, true), Leaf::getValueLong);
    }


    public String getISBN() {
        return get(Descriptor.ISBN, Leaf::getValueString);
    }

    public void setISBN(String isbn) {
        set(Descriptor.ISBN, leaf -> leaf.setValueString(isbn));
    }


    public String getTitle() {
        return get(Descriptor.TITLE, Leaf::getValueString);
    }

    public void setTitle(String title) {
        set(Descriptor.TITLE, leaf -> leaf.setValueString(title));
    }
}
