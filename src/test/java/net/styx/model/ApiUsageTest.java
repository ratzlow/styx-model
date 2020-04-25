package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Group;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.leaf.LongLeaf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;

// TODO (FRa) : (FRa): impl useGenericApiRaw()
public class ApiUsageTest {

    @Test
    void useSemanticApi() {
        Person person = populate(new Person(), "Frank", 87, Gender.MALE, new BigDecimal("100"));
        Assertions.assertEquals("Frank", person.getName());
        Assertions.assertEquals(87, person.getAge());
        Assertions.assertEquals(Gender.MALE, person.getGender());
        Assertions.assertEquals(new BigDecimal("100"), person.getIncome());

        populate(person, "Franziska", 101, Gender.FEMALE, BigDecimal.TEN);
        Assertions.assertEquals("Franziska", person.getName());
        Assertions.assertEquals(101, person.getAge());
        Assertions.assertEquals(Gender.FEMALE, person.getGender());
        Assertions.assertEquals(BigDecimal.TEN, person.getIncome());
        Assertions.assertNull(person.getDog());

        Dog dog = new Dog();
        dog.setName("Wuffi");
        dog.setAge(3);
        person.setDog(dog);
        Assertions.assertNotNull(person.getDog());
        Assertions.assertEquals("Wuffi", person.getDog().getName());
        Assertions.assertEquals(3, person.getDog().getAge());
    }

    @Test
    void useGenericApiWithTypes() {
        Group<Long, IdentifiableDataContainer<Long>> addresses = new Group<>(Descriptor.ADDRESS_GRP);
        IdentifiableDataContainer<Long> address = new IdentifiableDataContainer<>(
                Descriptor.ADDRESS,
                new LongLeaf(Descriptor.ADDRESS.getIDKey().orElseThrow(), 1L, true, true),
                Leaf::getValueLong
        );

        addresses.add(address);

        Collection<Address> properTypedAddresses = new Group<>(Descriptor.ADDRESS_GRP);
        properTypedAddresses.add(new Address(2L));
    }


    private Person populate(Person person, String name, int age, Gender gender, BigDecimal income) {
        person.setName(name);
        person.setAge(age);
        person.setGender(gender);
        person.setIncome(income);
        return person;
    }
}
