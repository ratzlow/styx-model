package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.BigDecimalLeaf;
import net.styx.model.tree.leaf.IntLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TODO (FRa) : (FRa): test rollback of add/remove ops
// TODO (FRa) : (FRa): test access to attributes not put into map, make it NPE safe by creating
//  member on demand (problem: components which need ID -> should only apply for elem in coll)
public class DataContainerTest {

    @Test
    void isDirty() {
        DataContainer dc = new DataContainer(Descriptor.ADDRESS);
        assertThat(dc.isChanged()).isFalse();
        assertThat(dc.isEmpty()).isTrue();

        assertThatThrownBy(() -> dc.getLeaf(Descriptor.AGE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dc.set(Descriptor.AGE, leaf -> leaf.setValueInt(87)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> dc.setLeaf(BigDecimalLeaf.EMPTY_VAL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(dc.isEmpty()).isTrue();
    }

    @Test
    void isDirtyAfterAddingNewLeaf() {
        DataContainer dc = new DataContainer(Descriptor.ADDRESS);
        var leaf = new StringLeaf(Descriptor.CITY);
        assertThat(leaf.isChanged()).isFalse();
        dc.setLeaf(leaf);

        assertThat(dc.isEmpty()).isTrue();
        assertThat(dc.isChanged()).isFalse();
    }

    @Test
    void isEmtpyWhenAllComponentsAreEmpty() {
        DataContainer parent = new DataContainer(Descriptor.PERSON);
        assertThat(parent.isEmpty()).isTrue();

        var leaf = new StringLeaf(Descriptor.NAME);
        assertThat(leaf.isEmpty());

        var child = new DataContainer(Descriptor.DOG);
        assertThat(child.isEmpty()).isTrue();

        parent.setLeaf(leaf);
        parent.setContainer(child);
        assertThat(parent.isEmpty()).isTrue();
        assertThat(parent.isChanged()).isFalse();

        // un/set leaf
        leaf.setValueString("XXX");
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        leaf.setValueString(null);
        assertThat(parent.isEmpty()).isTrue();
        assertThat(parent.isChanged()).isFalse();

        // un/set data container
        child.setLeaf(new StringLeaf(Descriptor.NAME, "Snoopy"));
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        DataContainer childContainer = parent.getContainer(Descriptor.DOG, DataContainer.class);
        childContainer.getLeaf(Descriptor.NAME).setValueString(null);
        assertThat(parent.isEmpty()).isTrue();
        assertThat(parent.isChanged()).isFalse();
    }

    @Test
    void isEmptyAfterComponentsAreRemoved() {
        DataContainer child = new DataContainer(Descriptor.DOG);
        child.setLeaf(new StringLeaf(Descriptor.NAME, "Snoopy"));
        assertThat(child.isEmpty()).isFalse();

        DataContainer parent = new DataContainer(Descriptor.PERSON);
        parent.setContainer(child);
        assertThat(child.isEmpty()).isFalse();
        assertThat(child.isChanged()).isTrue();

        assertThat(parent.remove(child.getNodeID().getDescriptor()))
                .as("Discard child component from parent container should signal success")
                .isTrue();
        assertThat(parent.isEmpty()).isTrue();
        assertThat(parent.isChanged()).isFalse();
    }


    @Test
    void isDirtyOnEmptyContainerAdd() {
        DataContainer dc = new DataContainer(Descriptor.ADDRESS);
        dc.setLeaf(new StringLeaf(Descriptor.CITY, "Zurich"));
        assertThat(dc.isChanged()).isTrue();
    }

    @Test
    void isDirtyOnAddOrModify() {
        StringLeaf street = new StringLeaf(Descriptor.STREET, "Main", false);
        assertThat(street.isChanged()).isFalse();

        DataContainer dc = new DataContainer(Descriptor.ADDRESS, Set.of(street));
        assertThat(dc.isChanged()).isFalse();
        assertThat(dc.isEmpty()).isFalse();

        assertThat(dc.getLeaf(Descriptor.STREET).getValueString()).isEqualTo("Main");
        assertThat(dc.getLeaf(Descriptor.STREET).isChanged()).isFalse();

        dc.getLeaf(Descriptor.STREET).setValueString("Sub");
        assertThat(dc.getLeaf(Descriptor.STREET).isChanged()).isTrue();

        // change back to initial value
        dc.getLeaf(Descriptor.STREET).setValueString("Main");
        assertThat(dc.getLeaf(Descriptor.STREET).isChanged()).isFalse();
        assertThat(street.isChanged()).isFalse();

        // add new attribute:
        //  - if it is clean -> container not dirty
        StringLeaf city = new StringLeaf(Descriptor.CITY);
        assertThat(city.isChanged()).isFalse();
        dc.setLeaf(city);
        assertThat(dc.isChanged()).isFalse();

        //  - if it is dirty -> container is dirty
        dc.set(Descriptor.CITY, leaf -> leaf.setValueString("Leipzig"));
        assertThat(city.isChanged()).isTrue();
        assertThat(dc.isChanged()).isTrue();

        //  reset to previous unset value -> container clean
        dc.set(Descriptor.CITY, leaf -> leaf.setValueString(null));
        assertThat(city.isChanged()).isFalse();
        assertThat(dc.isChanged()).isFalse();
    }

    @Test
    void groupAddAndClear() {

        DataContainer person = new DataContainer(Descriptor.PERSON);
        assertThat(person.isEmpty()).isTrue();

        Group books = new Group<>(Descriptor.BOOK_GRP);
        person.setGroup(books);
        assertThat(person.isEmpty()).isTrue();

        Book book = new Book(1L);
        books.add(book);
        assertThat(person.isEmpty()).isFalse();
        assertThat(person.isChanged()).isTrue();

        books.clear();
        assertThat(person.isEmpty()).isTrue();
        assertThat(person.isChanged()).isFalse();
    }

    @Test
    void groupCommitRollback() {
        Book firstBook = new Book(1);
        Group<?> books = new Group<>(Descriptor.BOOK_GRP, Set.of(firstBook));
        assertThat(books.isChanged())
                .as("Group with only empty initial elements cannot have changes!")
                .isFalse();

        firstBook.setTitle("Life of Brian");
        assertThat(books.isChanged()).isTrue();

        Container person = new DataContainer(Descriptor.PERSON);
        person.setGroup(books);
        assertThat(books.isChanged()).isTrue();
        assertThat(person.isEmpty()).isFalse();

        person.commit();
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();
        assertThat(person.getGroup(Descriptor.BOOK_GRP)).hasSize(1);

        person.getGroup(Descriptor.BOOK_GRP, Book.class).add(new Book(2));
        assertThat(person.getGroup(Descriptor.BOOK_GRP)).hasSize(2);

        person.rollback();
        assertThat(person.getGroup(Descriptor.BOOK_GRP)).hasSize(1);
        assertThat(person.getGroup(Descriptor.BOOK_GRP, Book.class)).containsExactly(firstBook);
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();

        // add 2nd group and deal with 1 committed and 1 added group
        Group<Address> addresses = new Group<>(Descriptor.ADDRESS_GRP);
        addresses.add(new Address(1));
        person.setGroup(addresses);
        assertThat(person.isChanged()).isTrue();
        assertThat(person.isEmpty()).isFalse();

        person.commit();
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();
        assertThat(addresses.isChanged()).isFalse();
    }

    @Test
    void deleteGroup() {
        Group<Address> addresses = new Group<>(Descriptor.ADDRESS_GRP);
        addresses.add(new Address(1));

        Group<Book> books = new Group<>(Descriptor.BOOK_GRP);
        books.add(new Book(1));

        DataContainer person = new DataContainer(Descriptor.PERSON);
        person.setGroup(addresses);
        person.setGroup(books);

        person.commit();
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();

        person.getGroup(Descriptor.BOOK_GRP).clear();
        assertThat(person.getGroup(Descriptor.BOOK_GRP)).isEmpty();
        assertThat(person.isChanged()).isTrue();
    }


    @DisplayName("Propagate or discard changes to graph")
    @Nested
    class StatefulOperations {
        DataContainer personParent;
        DataContainer dogChild;
        Leaf personNameLeaf;

        @BeforeEach
        void before() {
            dogChild = new DataContainer(Descriptor.DOG);
            dogChild.setLeaf(new IntLeaf(Descriptor.AGE));
            dogChild.getLeaf(Descriptor.AGE).setValueInt(11);
            assertThat(dogChild.isChanged()).isTrue();

            personNameLeaf = new StringLeaf(Descriptor.NAME, "Pepe");
            assertThat(personNameLeaf.isChanged()).isTrue();

            personParent = new DataContainer(Descriptor.PERSON);
            personParent.setLeaf(personNameLeaf);
            personParent.setContainer(dogChild);
            assertThat(personParent.isChanged()).isTrue();
        }

        @Test
        void commitFromEmpty() {
            personParent.commit();
            assertThat(personParent.getLeaf(personNameLeaf.getNodeID().getDescriptor()).getValueString()).isEqualTo("Pepe");
            assertThat(personParent.getContainer(dogChild.getNodeID().getDescriptor()).getLeaf(Descriptor.AGE).getValueInt()).isEqualTo(11);

            assertThat(personParent.isChanged()).isFalse();
            assertThat(dogChild.isChanged()).isFalse();
            assertThat(personNameLeaf.isChanged()).isFalse();
            assertThat(personParent.isEmpty()).as("New values are persisted to model!").isFalse();
        }


        @Test
        void commitMultipleTimes() {
            personParent.commit();
            assertThat(personParent.isChanged()).as("As first commit causes effect!").isFalse();

            personParent.commit();
            assertThat(personParent.isChanged()).as("Second commit is idempotent!").isFalse();

            personParent.getLeaf(Descriptor.NAME).setValueString("Joe");
            assertThat(personNameLeaf.getValueString()).isEqualTo("Joe");

            dogChild.getLeaf(Descriptor.AGE).setValueInt(17);
            assertThat(personParent.getContainer(Descriptor.DOG).getLeaf(Descriptor.AGE).getValueInt()).isEqualTo(17);

            assertThat(personParent.isChanged()).isTrue();
            personParent.rollback();
            assertThat(personParent.isChanged()).as("First rollback causes effect!").isFalse();
            assertThat(personNameLeaf.getValueString()).isEqualTo("Pepe");
            assertThat(personParent.getContainer(Descriptor.DOG).getLeaf(Descriptor.AGE).getValueInt()).isEqualTo(11);

            personParent.rollback();
            assertThat(personParent.isChanged()).as("Second rollback is idempotent!").isFalse();
        }

        @Test
        void rollbackToEmpty() {
            personParent.rollback();
            assertThat(personParent.getLeaf(personNameLeaf.getNodeID().getDescriptor()).getValueString()).isNull();
            assertThat(personParent.getContainer(dogChild.getNodeID().getDescriptor()).getLeaf(Descriptor.AGE).getValueInt())
                    .as("Native type cannot be null as default")
                    .isEqualTo(IntLeaf.EMPTY_VAL.getValueInt());
            assertThat(personParent.isChanged()).isFalse();
            assertThat(dogChild.isChanged()).isFalse();
            assertThat(personNameLeaf.isChanged()).isFalse();
            assertThat(personParent.isEmpty()).as("New values are discarded from model! " +
                    "Even though wrapper classes are preserved so far!").isTrue();
        }

        /*
        [] -> +name +dog.age -> rollback == [] // add
        [name, dog.age] -> +income %dog.name  // add, mod
        [name, income, dog.age] -> -income +dog.name -dog  // add, rm
        [name, income, dog.age] -> %income -income +dog.name -dog // add, rm, mod
        [name, income, dog.age] -> -dog.name(=null) +dog.name  // add, rm
        [name, income, dog.age] -> +dog.name -dog.name(=null) // add, rm
        [name, income, dog.age] -> -name +name // add, rm
        [name, income, dog.age] -> %name -name // mod, rm
        [name, income, dog.age] -> +name %name -name -name  // add, mod, rm
        [name, income, dog.age] -> +books(2) // add
        [name, income, dog.age] -> +books(0) // add
        [name, income, dog.age, books(2)] -> -books // add
        [name, income, dog.age, books(2)] -> +books(=null) // add
        [name, income, dog.age, books(2)] -> -books(1) // add
        [name, income, dog.age, books(2)] -> +book  // add
        [name, income, dog.age, books(0)] -> +book -book // add

        @Test
        void rollbackToInitial() {
        }
        */
    }
}
