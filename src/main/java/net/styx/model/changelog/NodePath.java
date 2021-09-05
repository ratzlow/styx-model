package net.styx.model.changelog;

import net.styx.model.meta.NodeDef;
import net.styx.model.meta.NodeID;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Path to a node starting at the root of the tree.
 */
public class NodePath<T extends NodeDef<?>> {
    private final List<NodeID<?>> nodeIDs;

    public NodePath(NodeID<T> leaf) {
        nodeIDs = List.of(leaf);
    }

    public NodePath(NodePath<?> parent, NodeID<T> leaf) {
        nodeIDs = new ArrayList<>(parent.nodeIDs);
        nodeIDs.add(leaf);
    }

    @SuppressWarnings("unchecked")
    public NodeID<T> getLeaf() {
        return (NodeID<T>) nodeIDs.get(nodeIDs.size()-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodePath<?> nodePath = (NodePath<?>) o;
        return nodeIDs.equals(nodePath.nodeIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeIDs);
    }

    @Override
    public String toString() {
        return "NodePath{" +
                "nodeIDs=" + nodeIDs +
                '}';
    }
}
