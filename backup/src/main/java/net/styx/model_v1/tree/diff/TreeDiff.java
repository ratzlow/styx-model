package net.styx.model_v1.tree.diff;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Leaf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TreeDiff {

    private final Step root;

    public TreeDiff(Step root) {
        Objects.requireNonNull(root);

        this.root = root;
    }

    public Step getRoot() {
        return root;
    }

    public static class Step implements Iterable<Step> {

        private final NodeID nodeID;
        private final Operation operation;
        private final List<Step> children;

        public Step(NodeID nodeID, Operation operation, List<Step> children) {
            this.nodeID = nodeID;
            this.operation = operation;
            this.children = children;
        }

        public Step(NodeID nodeID, Operation operation) {
            this(nodeID, operation, Collections.emptyList());
        }

        @Override
        public Iterator<Step> iterator() { return children.iterator(); }

        public NodeID getNodeID() { return nodeID; }

        public Operation getOperation() { return operation; }
    }


    public static class LeafDiff extends Step {
        private Leaf leaf;

        public LeafDiff(NodeID nodeID, Operation operation, Leaf leaf) {
            super(nodeID, operation);
            this.leaf = leaf;
        }

        public Leaf getLeaf() { return leaf; }
    }


    public enum Operation { UNCHANGED, ADD, REMOVE, UPDATE }
}
