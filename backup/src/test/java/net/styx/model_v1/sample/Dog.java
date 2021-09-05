package net.styx.model_v1.sample;

import net.styx.model_v1.tree.ContainerMixin;
import net.styx.model_v1.tree.Container;
import net.styx.model_v1.tree.DefaultContainer;
import net.styx.model_v1.tree.Leaf;

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
