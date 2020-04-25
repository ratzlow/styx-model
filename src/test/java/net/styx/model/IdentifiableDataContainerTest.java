package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.DataContainer;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.leaf.LongLeaf;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;


public class IdentifiableDataContainerTest {

    @DisplayName("Ensure stable IDs")
    @Nested
    class StatefulIDContract {
        @Test
        void commitPayloadAndDirtyID() {
            DataContainer dc = commitRollBackTemplate(DataContainer::commit, true, false);
            assertThat(dc.getLeaf(Descriptor.ISBN).getValueString()).isNotNull();
        }

        @Test
        void commitPayloadAndCleanID() {
            DataContainer dc = commitRollBackTemplate(DataContainer::commit, false, false);
            assertThat(dc.getLeaf(Descriptor.ISBN).getValueString()).isNotNull();
        }

        @Test
        void rollbackPayloadButLeaveIdDirty() {
            DataContainer dc = commitRollBackTemplate(DataContainer::rollback, true, true);
            assertThat(dc.getLeaf(Descriptor.ISBN).getValueString()).isNull();
        }

        @Test
        void rollbackPayloadButLeaveIdClean() {
            DataContainer dc = commitRollBackTemplate(DataContainer::rollback, false, false );
            assertThat(dc.getLeaf(Descriptor.ISBN)).isNull();
        }

        private DataContainer commitRollBackTemplate(Consumer<DataContainer> dcOp,
                                                     boolean markDirty,
                                                     boolean isDirty) {
            long idVal = 1L;
            String isbnVal = "12345678";
            Leaf id = new LongLeaf(Descriptor.ID, idVal, markDirty, true);
            IdentifiableDataContainer<Long> dc =
                    new IdentifiableDataContainer<>(Descriptor.BOOK, id, Leaf::getValueLong);
            dc.setLeaf(new StringLeaf(Descriptor.ISBN, isbnVal));

            dcOp.accept(dc);

            assertThat(dc.isChanged()).isFalse();
            assertThat(dc.getID())
                    .as("IDs are immutable and must keep their value!")
                    .isEqualTo(idVal);
            assertThat(dc.getLeaf(Descriptor.ID).isChanged()).isEqualTo(isDirty);

            return dc;
        }
    }


    @Test
    void createContainerWithID() {
        LongLeaf id = new LongLeaf(Descriptor.ID, 1L, true, true);
        IdentifiableDataContainer<Long> dc =
                new IdentifiableDataContainer<>(Descriptor.BOOK, id, Leaf::getValueLong);
        assertThat(dc.isEmpty()).isFalse();
        assertThat(dc.isChanged()).isTrue();
        assertThat(dc.getID()).isEqualTo(id.getValueLong());
    }

    @Test
    void createContainerWithoutID() {
        assertThatNullPointerException().describedAs("Wrapper is null").isThrownBy(
                () -> new IdentifiableDataContainer<>(Descriptor.BOOK, null, Leaf::getValueLong)
        );

        assertThatNullPointerException().describedAs("Wrapped value is missing").isThrownBy(
                () -> new IdentifiableDataContainer<>(Descriptor.BOOK,
                        new LongLeaf(Descriptor.ID, null), Leaf::getValueLong)
        );
    }

    @Test
    void containerIDsMustBeFrozen() {
        Leaf mutableID = new LongLeaf(Descriptor.ID, 2L);
        assertThat(mutableID.isFrozen()).isFalse();

        assertThatExceptionOfType(IllegalStateException.class)
                .describedAs("IDs must be supplied as immutable at construction time!")
                .isThrownBy( () -> new IdentifiableDataContainer<>(Descriptor.BOOK, mutableID, Leaf::getValueLong));


        Leaf id = new LongLeaf(Descriptor.ID, 1L, true, true);
        IdentifiableDataContainer<Long> dc =
                new IdentifiableDataContainer<>(Descriptor.BOOK, id, Leaf::getValueLong);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .describedAs("IDs are immutable! Applies also for leaked IDs!")
                .isThrownBy( () -> dc.getLeaf(Descriptor.ID).setValueLong(2L));

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .describedAs("IDs are immutable! Applies also for leaked IDs!")
                .isThrownBy( () -> dc.getLeaf(Descriptor.ID).setValueLong(null));
    }
}
