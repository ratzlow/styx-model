package net.styx.model.tree.leaf;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;

import java.util.Objects;

public abstract class AbstractLeaf<T> implements Leaf {

    private final NodeID nodeID;

    protected T current;
    protected T previous;
    private boolean changed = false;

    protected AbstractLeaf(NodeID nodeID) {
        this.nodeID = nodeID;
    }

    protected AbstractLeaf(NodeID nodeID, T val) {
        this(nodeID, val, true);
    }

    protected AbstractLeaf(NodeID nodeID, T val, boolean markDirty) {
        this.nodeID = nodeID;

        if (markDirty) {
            setValue(val);
        } else {
            current = val;
        }
    }


    protected void setValue(T val) {
        // nothing to mutate
        if (same(current, val)) {

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
    public NodeID getNodeID() {
        return nodeID;
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
            previous = null;
            changed = false;
        }
    }

    @Override
    public void rollback() {
        if (changed) {
            current = previous;
            previous = null;
            changed = false;
        }
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
                nodeID.equals(that.nodeID) &&
                Objects.equals(current, that.current) &&
                Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID, current, previous, changed);
    }
}
