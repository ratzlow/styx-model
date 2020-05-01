package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Book extends DefaultContainer {

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
        setLeaf(Descriptor.ISBN, leaf -> leaf.setValueString(isbn));
    }


    public String getTitle() {
        return get(Descriptor.TITLE, Leaf::getValueString);
    }

    public void setTitle(String title) {
        setLeaf(Descriptor.TITLE, leaf -> leaf.setValueString(title));
    }
}
