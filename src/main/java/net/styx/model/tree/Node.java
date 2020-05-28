package net.styx.model.tree;

import net.styx.model.meta.NodeID;

import java.util.Iterator;

public interface Node extends Stateful {

    NodeID getNodeID();

    Iterator<Node> children();

    /**
     * Remove attribute identified by #descriptor. Similar to setting an attribute to null.
     * Makes most sense to establish on delegating POJOs the set-to-null contract.
     *
     * @param childNodeID in dictionary that identifies node delete
     * @return true ... if specified child (element or attribute) was deleted and changed the state of this node,
     *         false ... if it was not part of the Node and not state change triggered
     */
    boolean remove(NodeID childNodeID);

    void accept(TreeWalker treeWalker);

    default void traverse(TreeWalker treeWalker) {
        traverseTree(this, treeWalker);
    }

    //-------------------------------------------------------------------------------------------
    // internal default implementation
    //-------------------------------------------------------------------------------------------

    private void traverseTree(Node node, TreeWalker treeWalker) {
        node.accept(treeWalker);

        for (Iterator<Node> iterator = node.children(); iterator.hasNext() && treeWalker.proceed(); ) {
            Node child = iterator.next();
            traverseTree(child, treeWalker);
        }

        treeWalker.onExit(node.getNodeID());
    }
}
