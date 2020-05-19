package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Container;
import net.styx.model.tree.DefaultContainer;
import net.styx.model.tree.Leaf;

import java.math.BigDecimal;
import java.util.Collection;

public class Person implements ContainerMixin {

    private static final Descriptor DESCRIPTOR = Descriptor.PERSON;

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


    public void setIncome(BigDecimal income) {
        setLeaf(Descriptor.INCOME, leaf -> leaf.setValueBigDec(income));
    }

    public BigDecimal getIncome() {
        return getLeafValue(Descriptor.INCOME, Leaf::getValueBigDec);
    }


    public void setGender(Gender gender) {
        setLeaf(Descriptor.GENDER, leaf -> leaf.setValueEnum(gender));
    }

    public Gender getGender() {
        return getLeafValue(Descriptor.GENDER, Leaf::getValueEnum);
    }

    public void setDog(Dog dog) {
        setContainer(dog);
    }

    public Dog getDog() {
        return getContainer(Descriptor.DOG, Dog.class);
    }

    public Collection<Book> getBooks() {
        return getGroup(Descriptor.BOOK_GRP, Book.class);
    }

    //-------------------------------------------------------------------------------------------------
    // bridge 
    //-------------------------------------------------------------------------------------------------

    @Override
    public Container delegate() {
        return container;
    }
}
