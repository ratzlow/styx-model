package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.MutationControlMixin;

import java.util.Objects;

// TODO (FRa) : (FRa): minimize copying values by stable orgValue vs. newValue?
public abstract class AbstractLeaf<T> implements Leaf {

    private final Descriptor descriptor;
    private final MutationControlMixin mutationControl = new MutationControlMixin();

    protected T current;
    protected T previous;
    private boolean changed = false;

    protected AbstractLeaf(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    protected AbstractLeaf(Descriptor descriptor, T val) {
        this(descriptor, val, true, false);
    }

    protected AbstractLeaf(Descriptor descriptor, T val, boolean markDirty) {
        this(descriptor, val, markDirty, false);
    }

    protected AbstractLeaf(Descriptor descriptor, T val, boolean markDirty, boolean markFrozen) {
        this.descriptor = descriptor;

        if (markDirty) {
            setValue(val);
        } else {
            current = val;
        }

        if (markFrozen) {
            mutationControl.freeze();
        }
    }


    protected void setValue(T val) {
        mutationControl.checkFrozen(this);

        // nothing to mutate
        if (same(current, val)) {
            return;
            // first value set, so apply
        } else if (!changed) {
            previous = current;
            current = val;
            changed = true;

            // reset to old value
        } else if (same(previous, val)) {
            current = previous;
            previous = null;
            changed = false;

            // repetitive setting new values
        } else {
            current = val;
        }
    }

    protected T getValue() {
        return current;
    }

    protected abstract boolean same(T current, T val);

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return Objects.toString(getValue());
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public boolean isEmpty() {
        return current == null;
    }

    @Override
    public void commit() {
        if (changed) {
            mutationControl.checkFrozen(this);
            previous = null;
            changed = false;
        }
    }

    @Override
    public void rollback() {
        if (changed) {
            mutationControl.checkFrozen(this);
            current = previous;
            previous = null;
            changed = false;
        }
    }

    //-------------------------------------------------------------------------------------------------
    // MutationControl
    //-------------------------------------------------------------------------------------------------

    @Override
    public boolean freeze() {
        return mutationControl.freeze();
    }

    @Override
    public boolean unfreeze() {
        return mutationControl.unfreeze();
    }

    @Override
    public boolean isFrozen() {
        return mutationControl.isFrozen();
    }

    //-------------------------------------------------------------------------------------------------
    // Object overrides
    //-------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLeaf<?> that = (AbstractLeaf<?>) o;
        return changed == that.changed &&
                descriptor == that.descriptor &&
                Objects.equals(current, that.current) &&
                Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, current, previous, changed);
    }
}
