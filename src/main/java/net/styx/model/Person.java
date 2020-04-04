package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Leaf;

import java.math.BigDecimal;
import java.util.Collection;

public class Person extends DataContainer {

    private static final Descriptor DESCRIPTOR = Descriptor.PERSON;

    public Person() {
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


    public void setIncome(BigDecimal income) {
        set(Descriptor.INCOME, leaf -> leaf.setValueBigDec(income));
    }

    public BigDecimal getIncome() {
        return get(Descriptor.INCOME, Leaf::getValueBigDec);
    }


    public void setGender(Gender gender) {
        set(Descriptor.GENDER, leaf -> leaf.setValueEnum(gender));
    }

    public Gender getGender() {
        return get(Descriptor.GENDER, Leaf::getValueEnum);
    }

    public void setDog(Dog dog) {
        setNode(dog);
    }

    public Dog getDog() {
        return getNode(Descriptor.DOG, Dog.class);
    }

    public Collection<Book> getBooks() {
        return getGroup(Descriptor.BOOK_GRP, Long.class, Book.class);
    }
}
