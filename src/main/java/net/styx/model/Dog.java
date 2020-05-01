package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Dog extends DefaultContainer {

    public static final Descriptor DESCRIPTOR = Descriptor.DOG;

    public Dog() {
        super(DESCRIPTOR);
    }

    //------------------------------------------------------------------------------------------
    // semantic API
    //------------------------------------------------------------------------------------------

    public void setName(String name) {
        setLeaf(Descriptor.NAME, leaf -> leaf.setValueString(name));
    }

    public String getName() {
        return get(Descriptor.NAME, Leaf::getValueString);
    }


    public void setAge(int age) {
        setLeaf(Descriptor.AGE, leaf -> leaf.setValueInt(age));
    }

    public int getAge() {
        return get(Descriptor.AGE, Leaf::getValueInt);
    }
}
