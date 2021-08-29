package net.styx.model.tree;

import net.styx.model.tree.diff.TreeDiff;

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
