package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.leaf.LongLeaf;

public class Address extends IdentifiableDataContainer<Long> {

    public static final Descriptor DESCRIPTOR = Descriptor.ADDRESS;

    public Address() {
        this(generateUuidLong());
    }

    public Address(Long id) {
        super(DESCRIPTOR,
                new LongLeaf(DESCRIPTOR.getIDKey().orElseThrow(), id, true, true),
                Leaf::getValueLong
        );
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
