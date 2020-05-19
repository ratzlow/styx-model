package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Dog implements ContainerMixin {

    public static final Descriptor DESCRIPTOR = Descriptor.DOG;
    private final Container container;

    public Dog() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Dog(Container container) {
        this.container = container;
    }

    //------------------------------------------------------------------------------------------
    // semantic API
    //------------------------------------------------------------------------------------------

    public void setName(String name) {
        setLeaf(Descriptor.NAME, leaf -> leaf.setValueString(name));
    }

    public String getName() {
        return getLeafValue(Descriptor.NAME, Leaf::getValueString);
    }


    public void setAge(int age) {
        setLeaf(Descriptor.AGE, leaf -> leaf.setValueInt(age));
    }

    public int getAge() {
        return getLeafValue(Descriptor.AGE, Leaf::getValueInt);
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
