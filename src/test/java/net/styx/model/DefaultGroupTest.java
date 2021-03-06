package net.styx.model;

import net.styx.model.sample.SampleDescriptor;
import net.styx.model.sample.Address;
import net.styx.model.tree.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.logging.Logger;

import static net.styx.model.tree.Nodes.anyChanged;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class DefaultGroupTest {

    static Logger LOGGER = Logger.getLogger(DefaultGroupTest.class.getName());

    private long addressSequence = 0;
    private DefaultGroup<Address> addresses;
    private Address first, second;

    @BeforeEach
    void before() {
        addresses = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        first = new Address(nextSeq());
        second = new Address(nextSeq());
    }

    @Test
    void sizeAndEmpty() {
        // collection impl test
        assertThat(addresses.isEmpty()).isTrue();
        assertThat(addresses.size()).isEqualTo(0);

        assertThat(addresses.add(first)).isTrue();
        assertThat(addresses).isNotEmpty();
        assertThat(addresses.size()).isEqualTo(1);

        assertThat(addresses.remove(first)).isTrue();
        assertThat(addresses.isEmpty()).isTrue();
        assertThat(addresses.size()).isEqualTo(0);
    }


    @Test
    void contains() {
        assertThat(addresses.contains(first)).isFalse();
        assertThat(addresses.isEmpty()).isTrue();

        assertThat(addresses.add(first)).isTrue();
        assertThat(addresses.contains(first)).isTrue();
        assertThat(addresses.remove(first)).isTrue();
        assertThat(addresses.contains(first)).isFalse();
        assertThat(addresses.contains(second)).as("As neg test").isFalse();
        assertThat(addresses.isEmpty()).isTrue();
    }

    @Test
    void iterator() {
        assertThat(addresses.iterator().hasNext()).isFalse();

        addresses.add(first);
        Iterator<Address> iterator = addresses.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(first);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void iteratorRemoveWillRollback() {
        Collection<Address> addresses = newAddresses(false);
        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, addresses);
        group.commit();
        assertThat(group.size()).isEqualTo(addresses.size());
        assertThat(group).isNotEmpty();

        assertThatIllegalStateException().isThrownBy(() -> group.iterator().remove());
        assertThat(group.size()).isEqualTo(addresses.size());
    }

    @Test
    void toArray() {
        assertThat(addresses.toArray()).isEmpty();
        assertThat(addresses.toArray(new Address[0])).isEmpty();

        addresses.add(first);
        addresses.add(second);

        assertThat(addresses.toArray()).hasSize(2);
        assertThat(addresses.toArray(new Address[0])).hasSize(2);

        assertThat(addresses.toArray(i -> new Address[87])).hasSize(87);
    }

    @Test
    @DisplayName("addAll, removeAll, containsAll, retainAll")
    void massOperation() {
        assertThat(addresses.contains(first)).isFalse();
        assertThat(addresses.contains(second)).isFalse();
        assertThat(addresses.containsAll(List.of(first, second))).isFalse();
        assertThat(addresses.removeAll(List.of(first, second))).isFalse();
        assertThat(addresses.retainAll(List.of(first, second))).isFalse();

        addresses.add(first);
        assertThat(addresses.containsAll(List.of(first, second))).isFalse();
        assertThat(addresses.contains(first)).isTrue();

        addresses.add(second);
        assertThat(addresses.containsAll(List.of(first, second))).isTrue();

        assertThat(addresses.removeAll(List.of(first, second))).isTrue();
        assertThat(addresses).isEmpty();

        assertThat(addresses.addAll(List.of(first, second))).isTrue();
        assertThat(addresses.retainAll(List.of(first))).isTrue();
        assertThat(addresses.size()).isEqualTo(1);
        assertThat(addresses.iterator().next()).isEqualTo(first);
    }

    @Test
    void duplicateAdd() {
        // add dup elements
        Address hannover = newAddress("Hannover", "Messe 1", 1234);

        DefaultGroup<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        assertThat(group.add(hannover)).isTrue();
        assertThat(group).hasSize(1);

        assertThat(group.add(hannover))
                .as("Adding same element with same key again must not alter the container")
                .isFalse();
        assertThat(group).hasSize(1);

        hannover.setZip(567);
        assertThat(group.add(hannover))
                .as("Modified element but same key does not alter container")
                .isFalse();
        assertThat(group).hasSize(1);
    }

    @Test
    void clear() {
        addresses.clear();
        addresses.addAll(List.of(first, second));
        assertThat(addresses.size()).isEqualTo(2);
        addresses.clear();
        assertThat(addresses.size()).isEqualTo(0);
    }

    @Test
    void java8Extensions() {
        assertThat(addresses.removeIf(elem -> elem == first)).isFalse();

        addresses.addAll(List.of(first, second));
        assertThat(addresses.removeIf(elem -> elem == first)).isTrue();
        assertThat(addresses.contains(first)).isFalse();
        assertThat(addresses.contains(second)).isTrue();
    }

    @Test
    void dirtyCheckOnInit() {
        assertThat(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP).isChanged())
                .as("Empty group is always clean")
                .isFalse();

        Address berlin = newAddress("Berlin", "Kastanienstr. 10", 12345);
        assertThat(berlin.isChanged()).isTrue();
        DefaultGroup<Address> gr_1 = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, List.of(berlin));
        assertThat(anyChanged(gr_1))
                .as("Empty group with dirty element is dirty as well!")
                .isTrue();

        DefaultGroup<Address> gr_2 = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        Address paris = newAddress("Paris", "Eifelplatz 1", 2222);
        gr_2.add(paris);
        assertThat(gr_2.isChanged())
                .as("Group with later on added dirty elem is also dirty!")
                .isTrue();
    }

    @DisplayName("Remove() as reverse op to Add() will clear dirty flag")
    @Test
    void dirtyCheckOnAddRemoveOp() {
        DefaultGroup<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        Address basel = newAddress("Basel", "Rheinufer", 7001);
        group.add(basel);
        assertThat(group.isChanged())
                .as("added element marks group dirty")
                .isTrue();

        group.remove(basel);
        assertThat(group.isChanged())
                .as("removed same element so group is reset to initial state - clean")
                .isFalse();
    }

    @Test
    void dirtyCheck() {
        Collection<Address> initial = newAddresses(false);
        List<Address> initialReversed = new ArrayList<>(initial);
        Collections.reverse(initialReversed);

        assertThat(initial).noneMatch(StatefulNode::isChanged);
        assertThat(initial).noneMatch(StatefulNode::isEmpty);

        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, initial);
        assertThat(group.isEmpty()).isFalse();
        assertThat(group.isChanged()).as("Group was not yet committed").isTrue();
        assertThat(anyChanged(group)).isTrue();

        group.clear();
        assertThat(group).isEmpty();
        assertThat(group.isChanged()).isTrue();

        assertThat(group.addAll(initialReversed)).isTrue();
        assertThat(group.isChanged())
                .as("Just add the elements as they existed before but with different ordering")
                .isFalse();

        Collection<Address> moreSimilarAddresses = newAddresses(false);
        assertThat(moreSimilarAddresses).doesNotContain(initial.toArray(new Address[0]));

        group.clear();
        group.addAll(moreSimilarAddresses);
        assertThat(group.isChanged()).as("Elements look the same but have different IDs").isTrue();
    }

    @Test
    void rollbackAfterRemove() {
        Collection<Address> col = newAddresses(false);
        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, col);
        group.commit();
        assertThat(anyChanged(group)).isFalse();

        Address firstAddress = group.iterator().next();
        group.remove(firstAddress);
        assertThat(anyChanged(group)).isTrue();

        group.rollback();
        assertThat(anyChanged(group)).isFalse();
    }


    @Test
    void dirtyCheckOnInlineChange() {
        Collection<Address> col = newAddresses(false);
        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, col);

        assertThat(anyChanged(group.toArray(new Node[0]))).isFalse();
        assertThat(group.isChanged()).isTrue();
        assertThat(anyChanged(group)).isTrue();

        group.commit();
        assertThat(anyChanged(group)).isFalse();
        Address address = newAddress("SinCity", "MainStr", 12345);
        group.add(address);
        assertThat(group.contains(address)).isTrue();
        assertThat(anyChanged(group)).isTrue();

        group.remove(address);
        assertThat(anyChanged(group)).isFalse();

        Address firstAddress = group.iterator().next();
        firstAddress.setCity("SinCity");
        assertThat(anyChanged(group)).isTrue();
        assertThat(group.isChanged()).isFalse();
    }


    @Test
    void rollbackToEmpty() {
        Collection<Address> addresses = newAddresses(false);
        DefaultGroup<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP);
        group.addAll(addresses);
        assertThat(group.isEmpty()).isFalse();
        assertThat(group.isChanged()).isTrue();

        group.rollback();

        assertThat(group.isEmpty()).isTrue();
        assertThat(group.isChanged()).isFalse();
    }

    @Test
    void rollbackToNonEmpty() {
        Collection<Address> addresses = newAddresses(false);
        assertThat(addresses).allMatch(a -> !a.isChanged());

        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, addresses);
        assertThat(group.isEmpty()).isFalse();
        assertThat(group.isChanged()).as("Unchanged elements in new Group").isTrue();

        group.iterator().next().setCity(UUID.randomUUID().toString());
        assertThat(group.isChanged()).isTrue();

        group.rollback();
        assertThat(addresses).allMatch(a -> !a.isChanged());
        assertThat(group.isEmpty()).isFalse();
        assertThat(group.isChanged()).isFalse();

        group.add(newAddress("Milano", "Via di Roma", 12));
        assertThat(group.isChanged()).isTrue();

        group.rollback();
        assertThat(group.isChanged()).isFalse();
        assertThat(group).containsExactlyInAnyOrderElementsOf(addresses);

        Address firstAddress = addresses.stream().findFirst().orElseThrow();
        group.remove(firstAddress);
        assertThat(group.size()).isEqualTo(addresses.size() - 1);
        assertThat(group).doesNotContain(firstAddress);
        assertThat(group.isChanged()).isTrue();

        group.rollback();
        assertThat(group).containsExactlyInAnyOrderElementsOf(addresses);
        assertThat(group.isChanged()).isFalse();
    }

    @Test
    void commitMultipleTimesToEmpty() {
        Collection<Address> addresses = newAddresses(true);
        assertThat(addresses).allMatch(StatefulNode::isChanged);

        Address first = addresses.iterator().next();

        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, addresses);
        group.addAll(addresses);
        assertThat(group.isEmpty()).isFalse();
        assertThat(group.isChanged())
                .as("Adding same elements (by key) to collection must not change it!")
                .isFalse();

        group.commit();
        assertThat(group.isChanged()).isFalse();
        assertThat(group.isEmpty()).isFalse();

        assertThat(addresses).allMatch(a -> !anyChanged(a));

        Address addedAddress = newAddress("Frankfurt", "Bahnhofstr.1", 222);
        group.add(addedAddress);
        assertThat(group.isChanged()).isTrue();
        assertThat(group.size()).isEqualTo(addresses.size() + 1);

        Address retrievedFirstAddress = group.stream()
                .filter(a -> a.getNodeID().equals(first.getNodeID()))
                .findFirst()
                .orElseThrow();
        String newCity = UUID.randomUUID().toString();
        retrievedFirstAddress.setCity(newCity);

        group.commit();
        assertThat(group.isChanged()).isFalse();
        assertThat(group.size()).isEqualTo(addresses.size() + 1);
        assertThat(first.getCity()).isEqualTo(newCity);

        group.remove(first);
        assertThat(group.size()).isEqualTo(addresses.size());
        assertThat(group).doesNotContain(first);
        assertThat(group.isChanged()).isTrue();

        group.commit();
        assertThat(group.size()).isEqualTo(addresses.size());
        assertThat(group).doesNotContain(first);
        assertThat(group.isChanged()).isFalse();
    }


    @Test
    void toStringWalker() {
        Group<?> addresses = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, newAddresses(true));
        String msg = Nodes.asString(addresses);
        assertThat(msg).isNotBlank();
        LOGGER.info(msg);
    }


    private Collection<Address> newAddresses(boolean markDirty) {
        Set<Address> addresses = Set.of(
                newAddress("Berlin", "Alex 1", 10317),
                newAddress("London", "MainStr 1", 24),
                newAddress("Paris", "Rue de la Sol 3", 111222)
        );

        if (!markDirty) {
            addresses.forEach(StatefulNode::commit);
        }

        return addresses;
    }

    private Address newAddress(String city, String street, int zip) {
        Address address = new Address(nextSeq());
        address.setCity(city);
        address.setStreet(street);
        address.setZip(zip);
        return address;
    }

    private long nextSeq() {
        return addressSequence++;
    }
}
