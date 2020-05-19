package net.styx.model.tree;

import net.styx.model.tree.traverse.ImmutabilityWalker;
import net.styx.model.tree.traverse.IsAllEmptyWalker;
import net.styx.model.tree.traverse.IsAnyChangedWalker;
import net.styx.model.tree.traverse.ToStringWalker;

public final class Nodes {

    /**
     * @param nodes root node of tree
     * @return true ... any node of the tree was dirty
     */
    public static boolean anyChanged(Node ... nodes) {
        TreeWalker treeWalker = forEach(nodes, new IsAnyChangedWalker());
        return !treeWalker.proceed();
    }

    /**
     * @param nodes root node of tree
     * @return true ... all nodes are empty!
     */
    public static boolean allEmpty(Node ... nodes) {
        TreeWalker treeWalker = forEach(nodes, new IsAllEmptyWalker());
        return treeWalker.proceed();
    }


    public static String asString(Node node) {
        ToStringWalker walker = new ToStringWalker();
        node.traverse(walker);
        return walker.toString();
    }


    public static <E extends Node> E freeze(E node) {
        ImmutabilityWalker walker = new ImmutabilityWalker();
        node.traverse(walker);
        return (E) walker.getImmutableNode();
    }


    private static <T extends TreeWalker> T forEach(Node[] nodes, T treeWalker) {
        for (int i = 0; i < nodes.length && treeWalker.proceed(); i++) {
            Node node = nodes[i];
            node.traverse(treeWalker);
        }
        return treeWalker;
    }
}
