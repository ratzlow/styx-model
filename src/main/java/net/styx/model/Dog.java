package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Leaf;

public class Dog extends DataContainer {

    public static final Descriptor DESCRIPTOR = Descriptor.DOG;

    public Dog() {
        super(DESCRIPTOR);
    }

    //------------------------------------------------------------------------------------------
    // semantic API
    //------------------------------------------------------------------------------------------

    public void setName(String name) {
        set(Descriptor.NAME, leaf -> leaf.setValueString(name));
    }

    public String getName() {
        return get(Descriptor.NAME, Leaf::getValueString);
    }


    public void setAge(int age) {
        set(Descriptor.AGE, leaf -> leaf.setValueInt(age));
    }

    public int getAge() {
        return get(Descriptor.AGE, Leaf::getValueInt);
    }
}
