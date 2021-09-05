package net.styx.model_v1.tree.leaf;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Leaf;

public class IntLeaf implements Leaf {

    public static final int EMPTY_VAL_NATIVE = -1;
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
        return current == EMPTY_VAL_NATIVE;
    }

    @Override
    public void commit() {
        if (changed) {
            previous = EMPTY_VAL_NATIVE;
            changed = false;
        }
    }

    @Override
    public void rollback() {
        if (changed) {
            current = previous;
            previous = EMPTY_VAL_NATIVE;
            changed = false;
        }
    }
}
