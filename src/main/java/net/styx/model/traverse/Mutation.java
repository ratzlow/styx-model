package net.styx.model.traverse;

import net.styx.model.tree.Leaf;

public class Mutation {
    final Operation operation;
    private Leaf leaf;
    private Step[] fullPath;
    private Step pathEnd;

    public Mutation(Operation operation, TreePath path, Leaf leaf) {
        this.leaf = leaf;
        this.operation = operation;
        this.fullPath = calculateFullPath(path, leaf);
        this.pathEnd = fullPath[fullPath.length - 1];
    }

    public Mutation(Operation operation, TreePath path) {
        this(operation, path, null);
    }

    public Mutation(Operation operation, Leaf leaf) {
        this(operation, TreePath.Builder.relative().build(), leaf);
    }

    public Leaf getLeaf() {
        return leaf;
    }

    public Step[] getFullPath() {
        return fullPath;
    }

    public Step getPathEnd() {
        return pathEnd;
    }

    private Step[] calculateFullPath(TreePath path, Leaf leaf) {
        final Step[] extendedPath;
        // TODO (FRa) : (FRa): add handling for abs/rela path
        if (leaf != null) {
            extendedPath = new Step[path.getSteps().length + 1];
            System.arraycopy(path.getSteps(), 0, extendedPath, 0, path.getSteps().length);
            extendedPath[extendedPath.length-1] = new Step(leaf.getDescriptor());
        } else {
            extendedPath = path.getSteps();
        }

        return extendedPath;
    }
}
