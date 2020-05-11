package net.styx.model.tree;

import net.styx.model.tree.traverse.*;

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

    public static Leaf freeze(Leaf leaf) {
        return new ImmutableLeaf(leaf);
    }

    public static Container freeze(Container container) {
        return new ImmutableContainer(container);
    }

    public static <E extends Node> Group<E> freeze(Group<E> group) {
        return new ImmutableGroup<>(group);
    }

    private static <T extends TreeWalker> T forEach(Node[] nodes, T treeWalker) {
        for (int i = 0; i < nodes.length && treeWalker.proceed(); i++) {
            Node node = nodes[i];
            node.accept(treeWalker);
        }
        return treeWalker;
    }
}
