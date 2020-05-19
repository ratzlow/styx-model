package net.styx.model.tree;

// TODO (FRa) : (FRa): can this interface be hidden from client code.
//  Client should use #traverse(TreeWalker)
public interface Traversable {

    void accept(TreeWalker treeWalker);
}
