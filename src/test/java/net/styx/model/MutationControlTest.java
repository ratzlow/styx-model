package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.Stateful;
import net.styx.model.tree.leaf.EnumLeaf;
import net.styx.model.tree.leaf.IntLeaf;
import net.styx.model.tree.leaf.LongLeaf;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

/**
 * Reflected contract:
 * - dirty & frozen, fail commit/rollback
 * - clean & frozen, success commit/rollback
 * - dirty & unfrozen, success commit/rollback
 * - clean & unfrozen, success commit/rollback
 *
 */
public class MutationControlTest {

    @Test
    void createImmutableItem() {
        long initialVal = 1L;
        Leaf leaf = new LongLeaf(Descriptor.UNDEF, initialVal, true, true);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> leaf.setValueLong(initialVal + 1L))
                .as("Immutable dirty item does not allow modification!");
        assertThat(leaf.getValueLong()).isEqualTo(initialVal);
    }


    @Test
    void checkContractOnDirtyAndFrozenVsStatefulOps() {
        checkContract(
                () -> new LongLeaf(Descriptor.UNDEF, 2L, true, true),
                true, true, false,
                "dirty & frozen, fail commit/rollback"
        );

        checkContract(
                () -> new LongLeaf(Descriptor.UNDEF, 2L, false, true),
                false, true, true,
                "clean & frozen, success commit/rollback"
        );

        checkContract(
                () -> new LongLeaf(Descriptor.UNDEF, 2L, true, false),
                true, false, true,
                "dirty & unfrozen, success commit/rollback"
        );

        checkContract(
                () -> new LongLeaf(Descriptor.UNDEF, 2L, false, false),
                false, false, true,
                "clean & unfrozen, success commit/rollback"
        );
    }


    private void checkContract(Supplier<Leaf> leafFactory,
                               boolean expectedDirty,
                               boolean expectedFrozen,
                               boolean expectedSuccessOnTX,
                               String msg) {

        Consumer<Consumer<Leaf>> check = methodDispatch -> {
            Leaf leaf = leafFactory.get();
            assertThat(leaf.isChanged()).isEqualTo(expectedDirty);
            assertThat(leaf.isFrozen()).isEqualTo(expectedFrozen);

            if (expectedSuccessOnTX) {
                assertThatCode(() -> methodDispatch.accept(leaf))
                        .as("Operation commit/rollback to succeed! " + msg)
                        .doesNotThrowAnyException();
            } else {
                assertThatExceptionOfType(UnsupportedOperationException.class)
                        .as("Operation commit/rollback to fail! " + msg)
                        .isThrownBy(() -> methodDispatch.accept(leaf));
            }
        };

        check.accept(Stateful::commit);
        check.accept(Stateful::rollback);
    }


    @Test
    void freezeUnfreezeSwitch() {
        freezeUnfreezeSwitchInternal(new LongLeaf(Descriptor.UNDEF), Leaf::setValueLong, Leaf::getValueLong,
                1L, 2L, 3L);

        freezeUnfreezeSwitchInternal(new IntLeaf(Descriptor.UNDEF), Leaf::setValueInt, Leaf::getValueInt,
                1, 2, 3);

        freezeUnfreezeSwitchInternal(new EnumLeaf(Descriptor.UNDEF), Leaf::setValueEnum, Leaf::getValueEnum,
                Color.BLUE, Color.GREEN, Color.YELLOW);
    }

    private <T> void freezeUnfreezeSwitchInternal(Leaf leaf,
                                                  BiConsumer<Leaf, T> setValue,
                                                  Function<Leaf, T> getValue,
                                                  T firstVal, T secondVal, T thirdVal) {

        assertThat(Set.of(firstVal, secondVal, thirdVal))
                .as("Test values have to be distinct!")
                .hasSize(3);

        assertThat(leaf.isFrozen()).isFalse();
        setValue.accept(leaf, firstVal);

        assertThat(leaf.freeze())
                .describedAs("Change the frozen state!")
                .isTrue();
        assertThat(leaf.freeze())
                .describedAs("Repetitive change is idempotent!")
                .isFalse();
        assertThat(leaf.isFrozen()).isTrue();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .describedAs("Object is labeled immutable!")
                .isThrownBy(() -> setValue.accept(leaf, secondVal));
        assertThat(getValue.apply(leaf)).isEqualTo(firstVal);


        assertThat(leaf.unfreeze())
                .describedAs("State change from frozen -> unfrozen")
                .isTrue();
        assertThat(leaf.unfreeze())
                .describedAs("Repetitive change is idempotent!")
                .isFalse();

        setValue.accept(leaf, thirdVal);
        assertThat(getValue.apply(leaf)).isEqualTo(thirdVal);
    }
}
