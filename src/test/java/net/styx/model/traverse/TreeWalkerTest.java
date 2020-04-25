package net.styx.model.traverse;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Group;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.Node;
import net.styx.model.tree.leaf.BigDecimalLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeWalkerTest {

    private static final TreeWalker WRITER = new TreeWalker();

    @DisplayName("Leaf operation on existing container")
    @Nested
    class ShallowLeafOnContainerTest extends AbstractOperationTest {

        @DisplayName("[] -> +name -> [name]")
        @Test()
        void addLeafToContainer() {
            StringLeaf nameLeaf = new StringLeaf(Descriptor.NAME, "Peter");
            WRITER.traverse(person, new Mutation(Operation.SET, nameLeaf));

            assertThat(person.getLeaf(Descriptor.NAME).getValueString()).isEqualTo(nameLeaf.getValueString());
            assertThat(person.getLeaf(Descriptor.NAME)).isSameAs(nameLeaf);
        }

        @DisplayName(
                "[name] -> %name -> [name]; " +
                        "[name] -> +income -> [name, income]" +
                        "[name, income] -> %name=null -> [name=null, income]"
        )
        @Test
        void updateLeafOnContainer() {
            StringLeaf nameLeaf = new StringLeaf(Descriptor.NAME, "Peter");
            person.setLeaf(nameLeaf);

            StringLeaf updateNameLeaf = new StringLeaf(Descriptor.NAME, "Bob");
            WRITER.traverse(person, new Mutation(Operation.SET, updateNameLeaf));

            assertThat(person.getLeaf(Descriptor.NAME).getValueString())
                    .as("Leaf has new value")
                    .isEqualTo(updateNameLeaf.getValueString());
            assertThat(person.getLeaf(Descriptor.NAME))
                    .as("Leaf instance preserved")
                    .isSameAs(nameLeaf);


            BigDecimalLeaf incomeLeaf = new BigDecimalLeaf(Descriptor.INCOME, new BigDecimal("87.5"));
            WRITER.traverse(person, new Mutation(Operation.SET, incomeLeaf));
            assertThat(person.getLeaf(Descriptor.INCOME).getValueBigDec())
                    .as("Init with sec attr")
                    .isSameAs(incomeLeaf.getValueBigDec());

            StringLeaf nullLeaf = new StringLeaf(Descriptor.NAME, null);
            WRITER.traverse(person, new Mutation(Operation.SET, nullLeaf));

            assertThat(person.getLeaf(Descriptor.NAME).getValueString())
                    .as("the value is null")
                    .isNull();
            assertThat(person.getLeaf(Descriptor.INCOME))
                    .as("Value is preserved!")
                    .isSameAs(incomeLeaf);
        }

        @DisplayName("[name, income] -> -income -> [name]")
        @Test
        void deleteExistingLeafOnContainer() {
            StringLeaf nameLeaf = new StringLeaf(Descriptor.NAME, "Peter");
            person.setLeaf(nameLeaf);

            WRITER.traverse(person, mutation(Operation.REMOVE, Descriptor.NAME));
            assertThat(person.getLeaf(Descriptor.NAME))
                    .as("Leaf removed from container")
                    .isNull();
        }
    }


    @DisplayName("Nested container operation on existing container")
    @Nested
    class ShallowContainerOnContainerTest extends AbstractOperationTest {

        @DisplayName(
                "[] -> +dog -> [dog];" +
                "[dog[name]] -> +dog -> [dog[name]];"
        )
        @Test
        void addContainerToContainer() {
            WRITER.traverse(person, mutation(Operation.SET, Descriptor.DOG));
            Node createdDog = person.getNode(Descriptor.DOG);
            assertThat(createdDog)
                    .as("Add new container instance")
                    .isNotNull();

            StringLeaf dogName = new StringLeaf(Descriptor.NAME, "Bello");
            WRITER.traverse(person, mutation(Operation.SET, dogName, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG).getLeaf(Descriptor.NAME))
                    .as("Set transitive person.dog.name")
                    .isSameAs(dogName);

            WRITER.traverse(person, mutation(Operation.SET, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG))
                    .as("Preserve nested container")
                    .isSameAs(createdDog);
            assertThat(person.getNode(Descriptor.DOG).getLeaf(Descriptor.NAME).getValueString())
                    .as("Nested container with leaf value")
                    .isSameAs(dogName.getValueString());
        }

        @DisplayName(
                "[dog] -> -dog -> [];" +
                        "[dog[name]] -> -dog -> [];")
        @Test
        void deleteContainerFromContainer() {
            WRITER.traverse(person, mutation(Operation.SET, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG))
                    .as("Add new container instance")
                    .isNotNull();

            WRITER.traverse(person, mutation(Operation.REMOVE, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG))
                    .as("Remove nested container instance")
                    .isNull();

            StringLeaf dogName = new StringLeaf(Descriptor.NAME, "Bello");
            WRITER.traverse(person, mutation(Operation.SET, dogName, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG).getLeaf(Descriptor.NAME)).isSameAs(dogName);

            WRITER.traverse(person, mutation(Operation.REMOVE, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG))
                    .as("Container with values is removed!")
                    .isNull();
        }
    }


    /*


    [books(1)] -> -book(2) -> [books(1,2)]
    [books(1)] -> +book(2).name -> [books(1,2)]
    [books(1)] -> %book(2).name -> [books(1)]
    [books(1)] -> -book(2).name -> [books(1)]
    [books(1)] -> +book(3).name -> [books(1,2,3)]
     */
    @DisplayName("Nested group operation on existing container")
    @Nested
    class ShallowGroupOnContainerTest extends AbstractOperationTest {

        @DisplayName(
                "[] -> +books() -> [books()];" +
                "[books()] -> -books() -> []"
        )
        @Test
        void addEmptyContainerToContainer() {
            WRITER.traverse(person, mutation(Operation.SET, Descriptor.BOOK_GRP));
            assertThat(person.getGroup(Descriptor.BOOK_GRP)).isNotNull().isEmpty();

            WRITER.traverse(person, mutation(Operation.REMOVE, Descriptor.BOOK_GRP));
            assertThat(person.getGroup(Descriptor.BOOK_GRP)).isNotNull();
            assertThat(person.getGroup(Descriptor.BOOK_GRP)).isEmpty();
            assertThat(person.isEmpty()).isTrue();
        }

        @DisplayName(
                "[] -> +book(1) -> [books(1)];" +
                "[books(1)] -> -book(1) -> [books()]"
        )
        @Test
        void addRemoveElements() {
            StringLeaf title = new StringLeaf(Descriptor.TITLE, "Best of ...");
            TreePath path = TreePath.Builder.relative().add(Descriptor.BOOK_GRP).add(Descriptor.BOOK, 27L).build();
            WRITER.traverse(person, new Mutation(Operation.SET, path, title));
            Group<?, ?> newGroup = person.getGroup(Descriptor.BOOK_GRP);
            assertThat(newGroup).isNotNull().isNotEmpty();
        }
    }


    // TODO (FRa) : (FRa): repetitive operations
    @DisplayName("Leaf operation on existing container")
    @Nested
    class DeepMixedOperationOnContainerTest extends AbstractOperationTest {

        @Test
        void addTransitiveContainerLeafToContainer() {
            StringLeaf dogName = new StringLeaf(Descriptor.NAME, "Bello");
            WRITER.traverse(person, mutation(Operation.SET, dogName, Descriptor.DOG));
            assertThat(person.getNode(Descriptor.DOG).getLeaf(Descriptor.NAME))
                    .as("Lazy create all nodes in the path")
                    .isSameAs(dogName);
        }
    }

    static class AbstractOperationTest {
        DataContainer person;

        @BeforeEach
        void before() {
            person = new DataContainer(Descriptor.PERSON);
            assertThat(person.isEmpty()).isTrue();
            assertThat(person.isChanged()).isFalse();
        }
    }

    private Mutation mutation(Operation operation, Descriptor ... descriptors) {
        return mutation(operation, null, descriptors);
    }

    private Mutation mutation(Operation operation, Leaf leaf, Descriptor ... descriptors) {
        TreePath path = TreePath.Builder.relative().add(descriptors).build();
        return new Mutation(operation, path, leaf);
    }
}
