package net.styx.model;

import net.styx.model.sample.SampleDescriptor;
import net.styx.model.sample.Address;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.LongLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import static net.styx.model.tree.Nodes.freeze;
import static org.assertj.core.api.Assertions.*;

public class MutationControlTest {

    static Logger LOGGER = Logger.getLogger(MutationControlTest.class.getCanonicalName());

    @Test
    void immutableLeaf() {
        Leaf leaf = new LongLeaf(SampleDescriptor.UNDEF, 11L);
        leaf.setValueLong(22L);
        assertThat(leaf.getValueLong()).isEqualTo(22);
        Leaf frozenLeaf = freeze(leaf);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenLeaf.setValueLong(33L));
        assertThat(leaf.getValueLong()).isEqualTo(22);
    }

    @Test
    void immutableContainer() {
        Container frozenContainer = freeze(new DefaultContainer(SampleDescriptor.PERSON));
        assertThat(frozenContainer.isEmpty()).isTrue();
        assertThatCode(frozenContainer::commit).doesNotThrowAnyException();
        assertThatCode(frozenContainer::rollback).doesNotThrowAnyException();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setContainer(new DefaultContainer(SampleDescriptor.DOG)));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setLeaf(new StringLeaf(SampleDescriptor.NAME)));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setLeaf(SampleDescriptor.NAME, leaf -> leaf.setValueString("xyz")));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setGroup(new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP)));
    }

    @Test
    void immutableGroup() {
        Collection<Address> addresses = new ArrayList<>();
        int addressCount = 3;
        for (int i = 0; i< addressCount; i++) {
            Address address = createAddress(i);
            addresses.add(address);
        }

        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP, addresses);
        Address changeAddress = createAddress(addressCount);

        group.add(changeAddress);
        assertThat(group.contains(changeAddress)).isTrue();

        group.remove(changeAddress);
        assertThat(group.contains(changeAddress)).isFalse();

        Group<Address> frozenGroup = freeze(group);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenGroup.add(changeAddress));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenGroup.remove(changeAddress));
    }

    @Test
    void deepImmutableGroup() {
        Group<Address> group = new DefaultGroup<>(SampleDescriptor.ADDRESS_GRP,
                Set.of(createAddress(1), createAddress(2)));

        Group<Address> frozenGroup = freeze(group);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenGroup.add(createAddress(3)));

        LOGGER.info(() -> Nodes.asString(frozenGroup));

        Address address = frozenGroup.iterator().next();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .as("No change at intermediate level!")
                .isThrownBy(() -> frozenGroup.add(createAddress(3)));

        int newZip = address.getZip() * 2;
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .as("Nodes on all hierarchies must be wrapped to be immutable! Here leafs")
                .isThrownBy(() -> address.setZip(newZip));
        assertThat(frozenGroup).noneMatch(a -> a.getZip() == newZip);
    }


    private Address createAddress(int idx) {
        Address address = new Address(idx);
        address.setCity("City_" + 1);
        address.setStreet("Street_" + 1);
        address.setZip(1);
        return address;
    }
}
