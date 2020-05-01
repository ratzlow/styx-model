package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Leaf;

public class Book extends DataContainer {

    public static final Descriptor DESCRIPTOR = Descriptor.BOOK;

    public Book() {
        super(DESCRIPTOR);
    }

    public Book(long id) {
        super(DESCRIPTOR, id);
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
