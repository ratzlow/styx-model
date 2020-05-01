package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Leaf;

public class Address extends DataContainer {

    public static final Descriptor DESCRIPTOR = Descriptor.ADDRESS;

    public Address() {
        super(DESCRIPTOR);
    }

    public Address(long id) {
        super(DESCRIPTOR, id);
    }

    public String getStreet() {
        return get(Descriptor.STREET, Leaf::getValueString);
    }

    public void setStreet(String street) {
        set(Descriptor.STREET, leaf -> leaf.setValueString(street));
    }

    public String getCity() {
        return get(Descriptor.CITY, Leaf::getValueString);
    }

    public void setCity(String city) {
        set(Descriptor.CITY, leaf -> leaf.setValueString(city));
    }

    public int getZip() {
        return get(Descriptor.ZIP, Leaf::getValueInt);
    }

    public void setZip(int zip) {
        set(Descriptor.ZIP, leaf -> leaf.setValueInt(zip));
    }
}
