package net.styx.model.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Path to a node starting at the root of the tree.
 */
public class NodePath<T extends NodeType<?>> implements Comparable<NodePath<T>> {

    private final List<NodeID<?>> nodeIDs;

    //------------------------------------------- constructors ---------------------------------------------------------

    static NodePath<?> combine(NodePath<?>... fragments) {
        List<NodeID<?>> combinedIDs = new ArrayList<>();
        for (NodePath<?> fragment : fragments) {
            combinedIDs.addAll(fragment.nodeIDs);
        }
        return new NodePath<>(combinedIDs);
    }

    public NodePath(NodeID<T> leaf) {
        nodeIDs = List.of(leaf);
    }

    public NodePath(NodePath<?> parent, NodeID<T> leaf) {
        nodeIDs = new ArrayList<>(parent.nodeIDs);
        nodeIDs.add(leaf);
    }

    private NodePath(List<NodeID<?>> nodeIDs) {
        this.nodeIDs = nodeIDs;
    }

    //------------------------------------------- API ------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public NodeID<T> getLeaf() {
        return (NodeID<T>) nodeIDs.get(nodeIDs.size()-1);
    }

    NodePath<?> lowerThan(NodePath<?> from) {
        final List<NodeID<?>> lower;
        boolean hasSubPath = nodeIDs.size() > from.nodeIDs.size();
        if (!hasSubPath) {
            lower = List.of();
        } else if (!this.nodeIDs.subList(0, from.nodeIDs.size()).equals(from.nodeIDs)) {
            lower = List.of();
        } else {
            lower = this.nodeIDs.subList(from.nodeIDs.size(), this.nodeIDs.size());
        }

        return new NodePath<>(lower);
    }

    public List<NodeID<?>> getNodeIDs() {
        return Collections.unmodifiableList(nodeIDs);
    }

    /**
     * Compare 2 base cases:
     * 1. path of same length
     * 2. path of different length
     */
    @Override
    public int compareTo(NodePath<T> other) {
        if (other == this) return 0;

        int result = 0;
        // compare same level nodeIDs
        if (nodeIDs.size() == other.nodeIDs.size()) {
            for (int i=0; i < nodeIDs.size() && result == 0; i++) {
                result = nodeIDs.get(i).compareTo(other.nodeIDs.get(i));
            }

        // short path always wins as it will be more generic, higher up in the tree
        } else {
            result = nodeIDs.size() < other.nodeIDs.size() ? 1 : -1;
        }

        return result;
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
        return nodeIDs.toString();
    }
}
