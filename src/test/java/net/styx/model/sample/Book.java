package net.styx.model.sample;

import net.styx.model.tree.ContainerMixin;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Book implements ContainerMixin {

    public static final SampleDescriptor DESCRIPTOR = SampleDescriptor.BOOK;
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
        return getLeafValue(SampleDescriptor.ISBN, Leaf::getValueString);
    }

    public void setISBN(String isbn) {
        setLeaf(SampleDescriptor.ISBN, leaf -> leaf.setValueString(isbn));
    }


    public String getTitle() {
        return getLeafValue(SampleDescriptor.TITLE, Leaf::getValueString);
    }

    public void setTitle(String title) {
        setLeaf(SampleDescriptor.TITLE, leaf -> leaf.setValueString(title));
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
