package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Leaf;

public class IntLeaf implements Leaf {

    public static final Leaf EMPTY_VAL = new IntLeaf(Descriptor.UNDEF);
    final Descriptor descriptor;

    private int current = -1;
    private int previous = -1;
    private boolean changed = false;

    public IntLeaf(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setValueInt(int val) {
        // nothing to mutate
        if (current == val) return;

            // first value set, so apply
        else if (!changed) {
            previous = current;
            current = val;
            changed = true;

            // repetitive setting new values
        } else {
            current = val;
        }
    }

    @Override
    public int getValueInt() {
        return current;
    }

    @Override
    public String toString() {
        return Integer.toString(current);
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public boolean isEmpty() {
        return current == EMPTY_VAL.getValueInt();
    }

    @Override
    public void commit() {
        if (changed) {
            previous = -1;
            changed = false;
        }
    }

    @Override
    public void rollback() {
        if (changed) {
            current = previous;
            previous = -1;
            changed = false;
        }
    }
}
