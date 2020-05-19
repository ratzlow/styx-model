package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Book implements ContainerMixin {

    public static final Descriptor DESCRIPTOR = Descriptor.BOOK;
    private final Container container;


    public Book() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Book(long id) {
        this(new DefaultContainer(DESCRIPTOR, id));
    }

    public Book(Container container) {
        this.container = container;
    }

    public String getISBN() {
        return getLeafValue(Descriptor.ISBN, Leaf::getValueString);
    }

    public void setISBN(String isbn) {
        setLeaf(Descriptor.ISBN, leaf -> leaf.setValueString(isbn));
    }


    public String getTitle() {
        return getLeafValue(Descriptor.TITLE, Leaf::getValueString);
    }

    public void setTitle(String title) {
        setLeaf(Descriptor.TITLE, leaf -> leaf.setValueString(title));
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
