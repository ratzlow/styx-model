package net.styx.model;

import net.styx.model.meta.NodeID;
import net.styx.model.sample.Address;
import net.styx.model.sample.SampleDescriptor;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.styx.model.tree.Nodes.anyChanged;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test behaviour of classes backed by {@link net.styx.model.tree.MapStore}
 */
// TODO (FRa) : (FRa): test rollback of add/remove ops
// TODO (FRa) : (FRa): check what tests can be consolidated Container/Group
public class MapStoreUsageTest {

    @Nested
    class SymmetricAddRemove {

        @Test
        void emptyContainer() {
            DefaultContainer parent = new DefaultContainer(SampleDescriptor.ADDRESS);
            NodeID childNodeID = SampleDescriptor.STREET;
            Runnable modification = () -> parent.setLeaf(childNodeID, leaf -> leaf.setValueString("Main"));

            removeIsSymmetricToAddOnEmptyNode(parent, modification, childNodeID);
        }

        @Test
        void emptyGroup() {
            Group<Container> parent = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
            NodeID childNodeID = SampleDescriptor.ADDRESS;
            Runnable modification = () -> parent.add(new DefaultContainer(SampleDescriptor.ADDRESS));

            removeIsSymmetricToAddOnEmptyNode(parent, modification, childNodeID);
        }

        @Test
        void initializedContainer() {
            Container parent = new DefaultContainer(SampleDescriptor.ADDRESS,
                    Set.of(new StringLeaf(SampleDescriptor.STREET, "High")));
            NodeID childNodeID = SampleDescriptor.STREET;
            Runnable modification =
                    () -> parent.setLeaf(new StringLeaf(SampleDescriptor.STREET, "High", false));

            removeIsSymmetricToAddOnInitializedNode(parent, modification, childNodeID);
        }

        @Test
        void initializedGroup() {
            Address address = new Address(1);
            address.setCity("High");

            Group<Container> parent = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, Set.of(address));
            NodeID childNodeID = address.getNodeID();
            Runnable modification = () -> parent.add(address);

            removeIsSymmetricToAddOnInitializedNode(parent, modification, childNodeID);
        }

        /**
         * Start with initialized Node.
         *
         * node:(a) -a +a == node(a) ... after the intermediate operations,
         * initial change/dirty properties should apply
         */
        private void removeIsSymmetricToAddOnInitializedNode(Node parent,
                                                             Runnable parentModification,
                                                             NodeID childNodeID) {
            parent.commit();
            assertThat(parent.isChanged()).isFalse();
            assertThat(parent.isEmpty()).isFalse();
            assertThat(anyChanged(parent)).isFalse();

            parent.remove(childNodeID);
            assertThat(parent.isChanged()).isTrue();
            assertThat(parent.isEmpty()).isTrue();
            assertThat(anyChanged(parent)).isTrue();

            parentModification.run();
            assertThat(parent.isChanged())
                    .as("Clean attribute with same NodeID was re-applied!")
                    .isFalse();
            assertThat(parent.isEmpty()).isFalse();
            assertThat(anyChanged(parent)).isFalse();
        }

        /**
         * Start empty.
         * node:() +a -a == node() ... after the intermediate operations,
         * initial change/dirty properties should apply
         */
        private void removeIsSymmetricToAddOnEmptyNode(Node parent,
                                                       Runnable parentModification,
                                                       NodeID childNodeID) {
            // start with empty container
            assertThat(parent.isChanged()).isFalse();
            assertThat(parent.isEmpty()).isTrue();

            parentModification.run();
            assertThat(parent.isChanged()).isTrue();
            assertThat(parent.isEmpty()).isFalse();

            assertThat(parent.remove(childNodeID)).isTrue();
            assertThat(parent.isChanged()).isFalse();
            assertThat(parent.isEmpty()).isTrue();
        }
    }
}
