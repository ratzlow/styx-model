package net.styx.model.tree;

public interface Traversable {

    void accept(TreeWalker treeWalker);
}
