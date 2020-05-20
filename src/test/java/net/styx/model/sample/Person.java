package net.styx.model.sample;

import net.styx.model.tree.ContainerMixin;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

import java.math.BigDecimal;
import java.util.Collection;

public class Person implements ContainerMixin {

    private static final SampleDescriptor DESCRIPTOR = SampleDescriptor.PERSON;

    private final Container container;


    public Person() {
        this(new DefaultContainer(DESCRIPTOR));
    }

    public Person(Container container) {
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


    public void setIncome(BigDecimal income) {
        setLeaf(SampleDescriptor.INCOME, leaf -> leaf.setValueBigDec(income));
    }

    public BigDecimal getIncome() {
        return getLeafValue(SampleDescriptor.INCOME, Leaf::getValueBigDec);
    }


    public void setGender(Gender gender) {
        setLeaf(SampleDescriptor.GENDER, leaf -> leaf.setValueEnum(gender));
    }

    public Gender getGender() {
        return getLeafValue(SampleDescriptor.GENDER, Leaf::getValueEnum);
    }

    public void setDog(Dog dog) {
        setContainer(dog);
    }

    public Dog getDog() {
        return getContainer(SampleDescriptor.DOG, Dog.class);
    }

    public Collection<Book> getBooks() {
        return getGroup(SampleDescriptor.BOOK_GRP, Book.class);
    }

    //-------------------------------------------------------------------------------------------------
    // bridge 
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
