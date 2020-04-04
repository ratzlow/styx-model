package net.styx.model.tree;

import net.styx.model.meta.Descriptor;

import java.util.UUID;
import java.util.function.Function;

public class IdentifiableDataContainer<T>
        extends DataContainer
        implements Identifiable<T> {

    private final Function<Leaf, T> idGetter;

    /**
     * @param descriptor of container type
     * @param id prepopulated ID (wrapper)
     * @param idGetter type safe ID value accessor, callback needs to match Leaf type
     */
    public IdentifiableDataContainer(Descriptor descriptor, Leaf id, Function<Leaf, T> idGetter) {
        super(descriptor);

        if (id.isEmpty()) {
            throw new IllegalArgumentException("ID must have a value! " + id);
        }

        this.idGetter = idGetter;
        setLeaf(id);
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
