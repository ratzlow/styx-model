package net.styx.model.sample;

import net.styx.model.tree.ContainerMixin;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

public class Dog implements ContainerMixin {

    public static final SampleDescriptor DESCRIPTOR = SampleDescriptor.DOG;
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
        setLeaf(SampleDescriptor.NAME, leaf -> leaf.setValueString(name));
    }

    public String getName() {
        return getLeafValue(SampleDescriptor.NAME, Leaf::getValueString);
    }


    public void setAge(int age) {
        setLeaf(SampleDescriptor.AGE, leaf -> leaf.setValueInt(age));
    }

    public int getAge() {
        return getLeafValue(SampleDescriptor.AGE, Leaf::getValueInt);
    }

    //-------------------------------------------------------------------------------------------------
    // bridge
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
