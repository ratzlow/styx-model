package net.styx.model.tree;

import net.styx.model.meta.Descriptor;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The ID assigned to the container is invariant. It cannot be mutated, neither by an explicit change
 * nor by a {@link #rollback()} which will revert all _other_ attributes part of this instance.
 *
 *
 * @param <T> type of backing ID value
 */
public class IdentifiableDataContainer<T>
        extends DataContainer
        implements Identifiable<T> {

    private final Function<Leaf, T> idGetter;

    /**
     * @param descriptor of container type
     * @param id prepopulated ID (wrapper)
     * @param idValueGetter type safe ID value accessor, callback needs to match Leaf type
     */
    public IdentifiableDataContainer(Descriptor descriptor, Leaf id, Function<Leaf, T> idValueGetter) {
        super(descriptor, Set.of(id));

        if (id.isEmpty()) {
            throw new NullPointerException("ID must have a value! " + id);
        }

        if (!id.isFrozen()) {
            throw new IllegalStateException("ID must be immutable! " + id);
        }

        this.idGetter = idValueGetter;
    }

    /**
     * Like {@link DataContainer#commit()}. Since the ID is immutable it is required to:
     * - unfreeze
     * - perform commit of all attributes (including ID)
     * - refreeze
     */
    @Override
    public void commit() {
        aroundTxOp(super::commit);
    }

    /**
     * All attributes _except_ the ID will be rolled back.
     * The ID will preserve even it's (change) flags.
     */
    @Override
    public void rollback() {
        aroundTxOp(super::rollback);
    }

    private void aroundTxOp(Runnable txOp) {
        Descriptor idKey = getDescriptor().getIDKey().orElseThrow();
        Leaf idLeaf = getLeaf(idKey);
        idLeaf.unfreeze();
        txOp.run();
        idLeaf.freeze();
    }

    @Override
    public T getID() {
        Descriptor idKey = getDescriptor().getIDKey().orElseThrow();
        return idGetter.apply(getLeaf(idKey));
    }

    /**
     * @return positive number which should be unique
     * @link https://stackoverflow.com/questions/15184820/how-to-generate-unique-positive-long-using-uuid
     */
    protected static long generateUuidLong() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
