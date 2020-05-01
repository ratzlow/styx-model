package net.styx.model.tree;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;

import java.util.Objects;

public class IdxNodeID implements NodeID {
    private final Descriptor descriptor;
    private final long idx;

    public IdxNodeID(Descriptor descriptor, long idx) {
        Objects.requireNonNull(descriptor);
        this.descriptor = descriptor;
        this.idx = idx;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public long getIdx() {
        return idx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdxNodeID nodeID = (IdxNodeID) o;
        return idx == nodeID.idx &&
                descriptor == nodeID.descriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, idx);
    }
}
