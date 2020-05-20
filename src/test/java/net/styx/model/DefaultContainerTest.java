package net.styx.model;

import net.styx.model.sample.SampleDescriptor;
import net.styx.model.sample.Address;
import net.styx.model.sample.Book;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.BigDecimalLeaf;
import net.styx.model.tree.leaf.IntLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.styx.model.tree.Nodes.anyChanged;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatCode;

// TODO (FRa) : (FRa): test rollback of add/remove ops
// TODO (FRa) : (FRa): test access to attributes not put into map, make it NPE safe by creating
//  member on demand (problem: components which need ID -> should only apply for elem in coll)
public class DefaultContainerTest {

    @Test
    void unsetLeafsAreImmutable() {
        Container dc = new DefaultContainer(SampleDescriptor.ADDRESS);
        assertThat(dc.getLeaf(SampleDescriptor.STREET))
                .as("only return unset default value")
                .isNotNull();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .as("attribute is not initialized, so only shared immutable instance available")
                .isThrownBy(() -> dc.getLeaf(SampleDescriptor.STREET).setValueString("Invalid street!"));
        assertThat(dc.getLeafValue(SampleDescriptor.STREET, Leaf::getValueString))
                .as("Fetching default value").isNull();

        String newValue = "Valid street!";
        assertThatCode(() -> dc.setLeaf(new StringLeaf(SampleDescriptor.STREET, newValue)))
                .doesNotThrowAnyException();
        dc.getLeaf(SampleDescriptor.STREET).setValueString(newValue);

        assertThat(dc.getLeafValue(SampleDescriptor.STREET, Leaf::getValueString))
                .as("Fetching default value").isEqualTo(newValue);
    }


    @Test
    void isDirty() {
        Container dc = new DefaultContainer(SampleDescriptor.ADDRESS);
        assertThat(dc.isChanged()).isFalse();
        assertThat(dc.isEmpty()).isTrue();

        assertThatThrownBy(() -> dc.getLeaf(SampleDescriptor.AGE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dc.setLeaf(SampleDescriptor.AGE, leaf -> leaf.setValueInt(87)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> dc.setLeaf(new BigDecimalLeaf(SampleDescriptor.UNDEF)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(dc.isEmpty()).isTrue();
    }

    @Test
    void isDirtyAfterAddingNewLeaf() {
        Container dc = new DefaultContainer(SampleDescriptor.ADDRESS);
        var leaf = new StringLeaf(SampleDescriptor.CITY);
        assertThat(leaf.isChanged()).isFalse();
        dc.setLeaf(leaf);

        assertThat(dc.isEmpty()).isFalse();
        assertThat(dc.isChanged()).isTrue();
    }


    @Test
    void newGroupIsDirtyBeforeCommit() {
        Group<Container> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        assertThat(group.isChanged()).as("New empty uncommitted/rollback Group is not dirty").isFalse();
        group.commit();
        assertThat(group.isChanged()).as("Committed empty group is clean.").isFalse();

        Container address = new DefaultContainer(SampleDescriptor.ADDRESS);
        assertThat(address.isChanged()).as("A new empty Container does not count as changed!").isFalse();
        group.add(address);
        assertThat(group.isChanged()).as("Added element").isTrue();
        // TODO (FRa) : (FRa): rollback of newly created element: leave as changed?
    }


    @Test
    void isEmtpyWhenAllComponentsAreEmpty() {
        Container parent = new DefaultContainer(SampleDescriptor.PERSON);
        assertThat(parent.isEmpty()).isTrue();

        Leaf leaf = new StringLeaf(SampleDescriptor.NAME);
        assertThat(leaf.isEmpty());

        Container child = new DefaultContainer(SampleDescriptor.DOG);
        assertThat(child.isEmpty()).isTrue();

        parent.setLeaf(leaf);
        parent.setContainer(child);
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        // un/set leaf
        leaf.setValueString("XXX");
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        leaf.setValueString(null);
        assertThat(leaf.isEmpty()).isTrue();
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        // un/set data container
        child.setLeaf(new StringLeaf(SampleDescriptor.NAME, "Snoopy"));
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();

        Container childContainer = parent.getContainer(SampleDescriptor.DOG, DefaultContainer.class);
        childContainer.getLeaf(SampleDescriptor.NAME).setValueString(null);
        assertThat(childContainer.getLeaf(SampleDescriptor.NAME).isEmpty()).isTrue();
        assertThat(parent.isEmpty()).isFalse();
        assertThat(parent.isChanged()).isTrue();
    }

    @Test
    void isEmptyAfterComponentsAreRemoved() {
        Container child = new DefaultContainer(SampleDescriptor.DOG);
        child.setLeaf(new StringLeaf(SampleDescriptor.NAME, "Snoopy"));
        assertThat(child.isEmpty()).isFalse();

        Container parent = new DefaultContainer(SampleDescriptor.PERSON);
        parent.setContainer(child);
        assertThat(child.isEmpty()).isFalse();
        assertThat(child.isChanged()).isTrue();

        assertThat(parent.remove(child.getNodeID()))
                .as("Discard child component from parent container should signal success")
                .isTrue();
        assertThat(parent.isEmpty()).isTrue();
        assertThat(parent.isChanged()).isFalse();
    }


    @Test
    void isDirtyOnEmptyContainerAdd() {
        Container dc = new DefaultContainer(SampleDescriptor.ADDRESS);
        dc.setLeaf(new StringLeaf(SampleDescriptor.CITY, "Zurich"));
        assertThat(dc.isChanged()).isTrue();
    }

    @Test
    void isDirtyOnAddOrModify() {
        StringLeaf street = new StringLeaf(SampleDescriptor.STREET, "Main", false);
        assertThat(street.isChanged()).isFalse();

        Container dc = new DefaultContainer(SampleDescriptor.ADDRESS, Set.of(street));
        assertThat(anyChanged(dc)).isTrue();
        assertThat(dc.isEmpty()).isFalse();

        assertThat(dc.getLeaf(SampleDescriptor.STREET).getValueString()).isEqualTo("Main");
        assertThat(dc.getLeaf(SampleDescriptor.STREET).isChanged()).isFalse();

        dc.getLeaf(SampleDescriptor.STREET).setValueString("Sub");
        assertThat(dc.getLeaf(SampleDescriptor.STREET).isChanged()).isTrue();

        // change back to initial value
        dc.getLeaf(SampleDescriptor.STREET).setValueString("Main");
        assertThat(dc.getLeaf(SampleDescriptor.STREET).isChanged()).isFalse();
        assertThat(street.isChanged()).isFalse();

        // add new attribute:
        //  - if it is clean -> container not dirty
        StringLeaf city = new StringLeaf(SampleDescriptor.CITY);
        assertThat(city.isChanged()).isFalse();
        dc.setLeaf(city);
        assertThat(dc.isChanged()).isTrue();
        assertThat(anyChanged(dc)).isTrue();

        //  - if it is dirty -> container is dirty
        dc.setLeaf(SampleDescriptor.CITY, leaf -> leaf.setValueString("Leipzig"));
        assertThat(city.isChanged()).isTrue();
        assertThat(dc.isChanged()).isTrue();

        // TODO (FRa) : (FRa): test dirty container is clean after rm of added attribute
        // TODO (FRa) : (FRa): make this test part of committed container
        //  reset to previous unset value -> container clean
//        dc.setLeaf(Descriptor.CITY, leaf -> leaf.setValueString(null));
  //      assertThat(city.isChanged()).isFalse();
    //    assertThat(dc.isChanged()).isFalse();
    }

    @Test
    void groupAddAndClear() {
        Container person = new DefaultContainer(SampleDescriptor.PERSON);
        assertThat(person.isEmpty()).isTrue();

        Group books = new DefaultGroup<>(SampleDescriptor.BOOK_GRP);
        person.setGroup(books);
        assertThat(person.isEmpty()).isFalse();

        Book book = new Book(1L);
        books.add(book);
        assertThat(person.isEmpty()).isFalse();
        assertThat(person.isChanged()).isTrue();

        books.clear();
        assertThat(person.isEmpty()).isFalse();
        assertThat(person.isChanged()).isTrue();
    }

    @Test
    void groupCommitRollback() {
        Book firstBook = new Book(1);
        Group<?> books = new DefaultGroup<>(SampleDescriptor.BOOK_GRP, Set.of(firstBook));
        assertThat(books.isChanged())
                .as("New Group with empty initial elements accounts as changed!")
                .isTrue();

        firstBook.setTitle("Life of Brian");
        assertThat(books.isChanged()).isTrue();

        Container person = new DefaultContainer(SampleDescriptor.PERSON);
        person.setGroup(books);
        assertThat(books.isChanged()).isTrue();
        assertThat(person.isEmpty()).isFalse();

        person.commit();
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP)).hasSize(1);

        person.getGroup(SampleDescriptor.BOOK_GRP, Book.class).add(new Book(2));
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP)).hasSize(2);

        person.rollback();
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP)).hasSize(1);
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP, Book.class)).containsExactly(firstBook);
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();

        // add 2nd group and deal with 1 committed and 1 added group
        Group<Address> addresses = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
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
        Group<Address> addresses = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        addresses.add(new Address(1));

        Group<Book> books = new DefaultGroup<>(SampleDescriptor.BOOK_GRP);
        books.add(new Book(1));

        Container person = new DefaultContainer(SampleDescriptor.PERSON);
        person.setGroup(addresses);
        person.setGroup(books);

        person.commit();
        assertThat(person.isChanged()).isFalse();
        assertThat(person.isEmpty()).isFalse();

        person.getGroup(SampleDescriptor.BOOK_GRP).clear();
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP)).isEmpty();
        assertThat(person.getGroup(SampleDescriptor.BOOK_GRP).isChanged()).isTrue();
        assertThat(person.isChanged())
                .as("Root object is not affected by child change").isFalse();
        assertThat(anyChanged(person))
                .as("SubTree was changed!").isTrue();
    }


    @DisplayName("Propagate or discard changes to graph")
    @Nested
    class StatefulOperations {
        Container personParent;
        Container dogChild;
        Leaf personNameLeaf;

        @BeforeEach
        void before() {
            dogChild = new DefaultContainer(SampleDescriptor.DOG);
            dogChild.setLeaf(new IntLeaf(SampleDescriptor.AGE));
            dogChild.getLeaf(SampleDescriptor.AGE).setValueInt(11);
            assertThat(dogChild.isChanged()).isTrue();

            personNameLeaf = new StringLeaf(SampleDescriptor.NAME, "Pepe");
            assertThat(personNameLeaf.isChanged()).isTrue();

            personParent = new DefaultContainer(SampleDescriptor.PERSON);
            personParent.setLeaf(personNameLeaf);
            personParent.setContainer(dogChild);
            assertThat(personParent.isChanged()).isTrue();
        }

        @Test
        void commitFromEmpty() {
            personParent.commit();
            assertThat(personParent.getLeaf(personNameLeaf.getNodeID()).getValueString()).isEqualTo("Pepe");
            assertThat(personParent.getContainer(dogChild.getNodeID()).getLeaf(SampleDescriptor.AGE).getValueInt()).isEqualTo(11);

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

            personParent.getLeaf(SampleDescriptor.NAME).setValueString("Joe");
            assertThat(personNameLeaf.getValueString()).isEqualTo("Joe");

            dogChild.getLeaf(SampleDescriptor.AGE).setValueInt(17);
            assertThat(personParent.getContainer(SampleDescriptor.DOG).getLeaf(SampleDescriptor.AGE).getValueInt()).isEqualTo(17);

            assertThat(anyChanged(personParent)).isTrue();
            personParent.rollback();
            assertThat(personParent.isChanged()).as("First rollback causes effect!").isFalse();
            assertThat(personNameLeaf.getValueString()).isEqualTo("Pepe");
            assertThat(personParent.getContainer(SampleDescriptor.DOG).getLeaf(SampleDescriptor.AGE).getValueInt()).isEqualTo(11);

            personParent.rollback();
            assertThat(personParent.isChanged()).as("Second rollback is idempotent!").isFalse();
        }

        @Test
        void rollbackToEmpty() {
            assertThat(personNameLeaf.isChanged()).isTrue();
            assertThat(anyChanged(personParent)).isTrue();
            personParent.rollback();
            assertThat(anyChanged(personParent)).isFalse();
            assertThat(personNameLeaf.isChanged())
                    .as("Rollback disjoints newly added Nodes and does not rollback those.").isTrue();

            assertThat(personParent.getLeaf(personNameLeaf.getNodeID()).getValueString()).isNull();
            Container dog = personParent.getContainer(dogChild.getNodeID());
            assertThat(dog).as("Added container is discarded").isNull();

            Leaf dogAge = dogChild.getLeaf(SampleDescriptor.AGE);
            assertThat(dogAge.getValueInt())
                    .as("Disjoint Container tree is not affected by rollback mutation on root node")
                    .isNotEqualTo(IntLeaf.EMPTY_VAL_NATIVE);

            assertThat(personParent.isChanged()).isFalse();
            assertThat(anyChanged(personParent)).isFalse();

            assertThat(personParent.isEmpty())
                    .as("Rollback removed old values").isTrue();
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
