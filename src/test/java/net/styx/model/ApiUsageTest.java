package net.styx.model;

import net.styx.model.sample.SampleDescriptor;
import net.styx.model.sample.Address;
import net.styx.model.sample.Dog;
import net.styx.model.sample.Gender;
import net.styx.model.sample.Person;
import net.styx.model.tree.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiUsageTest {

    @Test
    void useSemanticApi() {
        Person person = populate(new Person(), "Frank", 87, Gender.MALE, new BigDecimal("100"));
        assertThat(person.getName()).isEqualTo("Frank");
        assertThat(person.getAge()).isEqualTo(87);
        assertThat(person.getGender()).isEqualTo(Gender.MALE);
        assertThat(person.getIncome()).isEqualTo(new BigDecimal("100"));

        populate(person, "Franziska", 101, Gender.FEMALE, BigDecimal.TEN);
        assertThat(person.getName()).isEqualTo("Franziska");
        assertThat(person.getAge()).isEqualTo(101);
        assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(person.getIncome()).isEqualTo(BigDecimal.TEN);
        assertThat(person.getDog()).isNull();

        Dog dog = new Dog();
        dog.setName("Wuffi");
        dog.setAge(3);
        person.setDog(dog);
        assertThat(person.getDog()).isNotNull();
        assertThat(person.getDog().getName()).isEqualTo("Wuffi");
        assertThat(person.getDog().getAge()).isEqualTo(3);
    }

    @DisplayName("Test type combinations of Group vs. Node!")
    @Test
    void groupTypingVariations() {
        Group<Leaf> leafs = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        Group<Container> containers = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        Group<Node> nodes = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        Group<DefaultGroup<Node>> groups = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, List.of());
        assertThat(groups).isEmpty();


        manipulate(containers, new Address());
        manipulate(nodes, new Address());

        Collection<Container> addresses1 = manipulate(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP), new Address());
        Collection<Address> addresses2 = manipulate(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP), new Address());
        Collection<Node> addresses3 = manipulate(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP), new Address());

        Collection<Node> addresses4 =
                manipulate(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP), new DefaultContainer(SampleDescriptor.ADDRESS));

        assertThat(List.of(addresses1, addresses2, addresses3, addresses4))
                .allMatch(g -> g.size() == 1);

        Group<?> unboundGroup;
        Group rawGroup;
        rawGroup = leafs;
        unboundGroup = leafs;
        assertThat(List.of(unboundGroup, rawGroup)).allMatch(Group::isEmpty);
    }

    private <E extends Node> Group<E> manipulate(Group<E> group, E element) {
        group.add(element);
        group.remove(element);
        group.add(element);

        return group;
    }

    private Person populate(Person person, String name, int age, Gender gender, BigDecimal income) {
        person.setName(name);
        person.setAge(age);
        person.setGender(gender);
        person.setIncome(income);
        return person;
    }
}
