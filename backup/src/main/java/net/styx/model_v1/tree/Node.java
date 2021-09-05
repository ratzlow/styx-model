package net.styx.model_v1.tree;

import net.styx.model_v1.meta.NodeID;

import java.util.Iterator;

public interface Node<T extends Node<T>> {

    NodeID getNodeID();

    Iterator<T> children();

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

    private void traverseTree(Node<T> node, TreeWalker treeWalker) {
        node.accept(treeWalker);

        for (Iterator<T> iterator = node.children(); iterator.hasNext() && treeWalker.proceed(); ) {
            T child = iterator.next();
            traverseTree(child, treeWalker);
        }

        treeWalker.onExit(node.getNodeID());
    }
}
