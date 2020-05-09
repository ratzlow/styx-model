package net.styx.model.tree;

import net.styx.model.tree.traverse.Operation;

public class NodeChange {
    private final Node node;
    private final Operation operation;

    public NodeChange(Node node, Operation operation) {
        this.node = node;
        this.operation = operation;
    }

    public Node getNodeID() {
        return node;
    }

    public Operation getOperation() {
        return operation;
    }
}
