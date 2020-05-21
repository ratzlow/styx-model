package net.styx.model.tree;

import net.styx.model.meta.NodeID;

import java.util.Iterator;

public interface Node extends Stateful {

    NodeID getNodeID();


    Iterator<Node> children();


    void accept(TreeWalker treeWalker);


    default void traverse(TreeWalker treeWalker) {
        traverseTree(this, treeWalker);
    }


    //-------------------------------------------------------------------------------------------
    // internal default implementation
    //-------------------------------------------------------------------------------------------

    private void traverseTree(Node node, TreeWalker treeWalker) {
        node.accept(treeWalker);

        for (Iterator<Node> iter = node.children(); iter.hasNext() && treeWalker.proceed(); ) {
            Node child = iter.next();
            traverseTree(child, treeWalker);
        }

        treeWalker.onExit(node.getNodeID());
    }
}
