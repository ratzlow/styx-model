package net.styx.model_v1.sample;

import net.styx.model_v1.tree.ContainerMixin;
import net.styx.model_v1.tree.Container;
import net.styx.model_v1.tree.DefaultContainer;
import net.styx.model_v1.tree.Leaf;

public class Address implements ContainerMixin {

    public static final SampleDescriptor DESCRIPTOR = SampleDescriptor.ADDRESS;
    
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
        return getLeafValue(SampleDescriptor.STREET, Leaf::getValueString);
    }

    public void setStreet(String street) {
        setLeaf(SampleDescriptor.STREET, leaf -> leaf.setValueString(street));
    }

    public String getCity() {
        return getLeafValue(SampleDescriptor.CITY, Leaf::getValueString);
    }

    public void setCity(String city) {
        setLeaf(SampleDescriptor.CITY, leaf -> leaf.setValueString(city));
    }

    public int getZip() {
        return getLeafValue(SampleDescriptor.ZIP, Leaf::getValueInt);
    }

    public void setZip(int zip) {
        setLeaf(SampleDescriptor.ZIP, leaf -> leaf.setValueInt(zip));
    }
    
    //-------------------------------------------------------------------------------------------------
    // bridge 
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
