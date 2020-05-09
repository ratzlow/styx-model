package net.styx.model.tree;

import net.styx.model.tree.traverse.IsAllEmptyWalker;
import net.styx.model.tree.traverse.IsAnyChangedWalker;

public final class Nodes {

    /**
     * @param nodes root node of tree
     * @return true ... any node of the tree was dirty
     */
    public static boolean anyChanged(Node... nodes) {
        TreeWalker treeWalker = forEach(nodes, new IsAnyChangedWalker());
        return !treeWalker.proceed();
    }

    /**
     * @param nodes root node of tree
     * @return true ... all nodes are empty!
     */
    public static boolean allEmpty(Node... nodes) {
        TreeWalker treeWalker = forEach(nodes, new IsAllEmptyWalker());
        return treeWalker.proceed();
    }


    private static <T extends TreeWalker> T forEach(Node[] nodes, T treeWalker) {
        for (int i = 0; i < nodes.length && treeWalker.proceed(); i++) {
            Node node = nodes[i];
            node.accept(treeWalker);
        }
        return treeWalker;
    }
}
