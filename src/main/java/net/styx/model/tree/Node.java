package net.styx.model.tree;

import net.styx.model.meta.NodeID;

import java.util.Iterator;

// TODO (FRa) : (FRa): impl Iterable?!
public interface Node extends Stateful, Traversable {

    NodeID getNodeID();

    Iterator<Node> children();

    default void traverse(TreeWalker treeWalker) {
        traverseTree(this, treeWalker);
    }

    private void traverseTree(Node node, TreeWalker treeWalker) {
        node.accept(treeWalker);

        for (Iterator<Node> iter = node.children(); iter.hasNext() && treeWalker.proceed(); ) {
            Node child = iter.next();
            traverseTree(child, treeWalker);
        }

        treeWalker.onExit(node.getNodeID());
    }
}
