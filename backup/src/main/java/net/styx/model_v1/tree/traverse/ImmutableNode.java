package net.styx.model_v1.tree.traverse;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Node;
import net.styx.model_v1.tree.StatefulNode;
import net.styx.model_v1.tree.TreeWalker;

/**
 * Support immutable access to particular node type instance.
 * Only immutability of this node level is guaranteed, not that of contained children.
 * For object graph all nodes have to be wrapped individually.
 */
public abstract class ImmutableNode<T extends StatefulNode> implements StatefulNode {

    protected T node;

    public ImmutableNode(T node) {
        this.node = node;
    }

    @Override
    public NodeID getNodeID() {
        return node.getNodeID();
    }

    @Override
    public boolean isChanged() {
        return node.isChanged();
    }

    @Override
    public boolean isEmpty() {
        return node.isEmpty();
    }

    @Override
    public void commit() {
        invoke(node::commit);
    }

    @Override
    public void rollback() {
        invoke(node::commit);
    }

    @Override
    public void accept(TreeWalker treeWalker) {
        node.accept(treeWalker);
    }

    //--------------------------------------------------------------------------------------
    // Object overrides
    //--------------------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && node.equals(obj);
    }

    @Override
    public String toString() {
        return node.toString();
    }

    protected boolean prevent() {
        throw new UnsupportedOperationException(exceptionMsg());
    }

    protected String exceptionMsg() {
        return "Cannot modify immutable node: " + node;
    }

    private void invoke(Runnable operation) {
        if (isEmpty()) {
            operation.run();
        } else {
            prevent();
        }
    }
}
