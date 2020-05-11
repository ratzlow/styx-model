package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.LongLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static net.styx.model.tree.Nodes.freeze;
import static org.assertj.core.api.Assertions.*;

// TODO (FRa) : (FRa): check transitive RO mode
public class MutationControlTest {

    @Test
    void immutableLeaf() {
        Leaf leaf = new LongLeaf(Descriptor.UNDEF, 11L);
        leaf.setValueLong(22L);
        assertThat(leaf.getValueLong()).isEqualTo(22);
        Leaf frozenLeaf = freeze(leaf);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenLeaf.setValueLong(33L));
        assertThat(leaf.getValueLong()).isEqualTo(22);
    }

    @Test
    void immutableContainer() {
        Container frozenContainer = freeze(new DefaultContainer(Descriptor.PERSON));
        assertThat(frozenContainer.isEmpty()).isTrue();
        assertThatCode(frozenContainer::commit).doesNotThrowAnyException();
        assertThatCode(frozenContainer::rollback).doesNotThrowAnyException();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setContainer(new DefaultContainer(Descriptor.DOG)));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setLeaf(new StringLeaf(Descriptor.NAME)));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setLeaf(Descriptor.NAME, leaf -> leaf.setValueString("xyz")));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> frozenContainer.setGroup(new DefaultGroup<>(Descriptor.ADDRESS_GRP)));
    }

    @Test
    void immutableGroup() {
        Collection<Address> addresses = new ArrayList<>();
        int addressCount = 3;
        for (int i = 0; i< addressCount; i++) {
            Address address = createAddress(i);
            addresses.add(address);
        }

        Group<Address> group = new DefaultGroup<>(Descriptor.ADDRESS_GRP, addresses);
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

    private Address createAddress(int idx) {
        Address address = new Address(idx);
        address.setCity("City_" + 1);
        address.setStreet("Street_" + 1);
        address.setZip(1);
        return address;
    }
}
