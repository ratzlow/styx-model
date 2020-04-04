package net.styx.model.tree;

public interface Stateful {

    /**
     * @return true ... initial value is different from current value
     *         false ... no recorded change to initial one
     */
    boolean isChanged();

    /**
     * @return true ... if set to $UNSET value which for objects is 'null'. Primitives define their
     *                  respective $UNSET constant to indicate "no value contained"
     */
    boolean isEmpty();

    /**
     * Current state will be persisted, means old state will be discarded. Mark it as clean.
     */
    void commit();

    /**
     * Current state will be discarded, means old state will be persisted. Mark it as clean.
     */
    void rollback();
}
