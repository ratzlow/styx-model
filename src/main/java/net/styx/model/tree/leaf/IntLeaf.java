package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.MutationControlMixin;

public class IntLeaf implements Leaf {

    public static final Leaf EMPTY_VAL = new IntLeaf(Descriptor.UNDEF);

    private static final int EMPTY_VAL_NATIVE = -1;
    private final MutationControlMixin mutationControl = new MutationControlMixin();
    private final NodeID nodeID;

    private int current = EMPTY_VAL_NATIVE;
    private int previous = EMPTY_VAL_NATIVE;
    private boolean changed = false;

    public IntLeaf(NodeID nodeID) {
        this(nodeID, EMPTY_VAL_NATIVE);
    }

    public IntLeaf(NodeID nodeID, int value) {
        this.nodeID = nodeID;
        setValueInt(value);
    }

    @Override
    public NodeID getNodeID() {
        return nodeID;
    }

    @Override
    public void setValueInt(int val) {
        mutationControl.checkFrozen(this);

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
    public void setValueLeaf(Leaf from) {
        setValueInt(from.getValueInt());
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

    @Override
    public void commit() {
        if (changed) {
            mutationControl.checkFrozen(this);
            previous = EMPTY_VAL_NATIVE;
            changed = false;
        }
    }

    @Override
    public void rollback() {
        if (changed) {
            mutationControl.checkFrozen(this);
            current = previous;
            previous = EMPTY_VAL_NATIVE;
            changed = false;
        }
    }
}
