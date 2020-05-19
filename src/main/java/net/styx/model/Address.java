package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Address implements ContainerMixin {

    public static final Descriptor DESCRIPTOR = Descriptor.ADDRESS;
    
    private final Container container;

    public Address() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Address(long id) {
        this(new DefaultContainer(DESCRIPTOR, id));
    }

    public Address(Container container) {
        this.container = container;
    }

    public String getStreet() {
        return getLeafValue(Descriptor.STREET, Leaf::getValueString);
    }

    public void setStreet(String street) {
        setLeaf(Descriptor.STREET, leaf -> leaf.setValueString(street));
    }

    public String getCity() {
        return getLeafValue(Descriptor.CITY, Leaf::getValueString);
    }

    public void setCity(String city) {
        setLeaf(Descriptor.CITY, leaf -> leaf.setValueString(city));
    }

    public int getZip() {
        return getLeafValue(Descriptor.ZIP, Leaf::getValueInt);
    }

    public void setZip(int zip) {
        setLeaf(Descriptor.ZIP, leaf -> leaf.setValueInt(zip));
    }
    
    //-------------------------------------------------------------------------------------------------
    // bridge 
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
