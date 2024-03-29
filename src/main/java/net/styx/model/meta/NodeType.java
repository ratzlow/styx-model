package net.styx.model.meta;

/**
 * @param <E> business entity type
 */
public interface NodeType<E> {

    int getID();

    String getDefaultName();

    /**
     * Nodes might be normalized before added to tree and change log.
     * @param value org
     * @return the normalized value
     */
    default E normalize(E value) {
        return value;
    }
}
