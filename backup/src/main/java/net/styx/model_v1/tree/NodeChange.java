package net.styx.model_v1.tree;

import net.styx.model_v1.tree.diff.TreeDiff;

public class NodeChange {
    private final Node node;
    private final TreeDiff.Operation operation;

    public NodeChange(Node node, TreeDiff.Operation operation) {
        this.node = node;
        this.operation = operation;
    }

    public Node getNodeID() {
        return node;
    }

    public TreeDiff.Operation getOperation() {
        return operation;
    }
}
