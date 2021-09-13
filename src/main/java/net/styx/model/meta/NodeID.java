package net.styx.model.meta;

import java.util.Objects;

public class NodeID<T extends NodeDef<?>> implements Comparable<NodeID<?>> {
    /**
     * Stable semantic ID per Node type.
     */
    private final int id;

    /**
     * Index can be:
     * - static for a fixed single component assigned at dictionary instantiation
     * - dynamic for a sub node (Components, Attributes) on creation time
     */
    private final int idx;

    private final String name;
    private final T nodeDef;

    public NodeID(T nodeDef) {
        this(0, nodeDef.getDefaultName(), nodeDef);
    }

    public NodeID(int idx, T nodeDef) {
        this(idx, nodeDef.getDefaultName(), nodeDef);
    }

    public NodeID(int idx, String name, T nodeDef) {
        this.id = nodeDef.getID();
        this.idx = idx;
        this.name = name;
        this.nodeDef = nodeDef;
    }

    public T def() {
        return nodeDef;
    }


    @Override
    public int compareTo(NodeID<?> o) {
        int result = Integer.compare(id, o.id);
        if (result == 0) {
            result = Integer.compare(idx, o.idx);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeID<?> nodeID = (NodeID<?>) o;
        return id == nodeID.id && idx == nodeID.idx;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idx);
    }

    @Override
    public String toString() {
        return name + '(' + id + ',' + idx+ ')';
    }
}
