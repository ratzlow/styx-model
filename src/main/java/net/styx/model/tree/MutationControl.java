package net.styx.model.tree;

// TODO (FRa) : (FRa): add to Node
public interface MutationControl {

    /**
     * Make the shallow item immutable. If leaking components themselves are mutable it is up to this
     * item, to what extend it gets rippled down.
     * If mutating operations are called item will throw {@link UnsupportedOperationException}
     *
     * @return true ... if it switched the from unfrozen to frozen
     */
    boolean freeze();

    /**
     * Make the shallow item mutable. If leaking components themselves are mutable it is up to this
     * item, to what extend it gets rippled down.
     *
     * @return true ... if it switched the from frozen to unfrozen
     */
    boolean unfreeze();

    /**
     * @return true ... if in immutable state
     */
    boolean isFrozen();
}
