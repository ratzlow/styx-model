package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;

public class Mutation {
    final Operation operation;
    private Leaf leaf;
    private NodeID[] fullPath;
    private NodeID pathEnd;

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

    public NodeID[] getFullPath() {
        return fullPath;
    }

    public NodeID getPathEnd() {
        return pathEnd;
    }

    private NodeID[] calculateFullPath(TreePath path, Leaf leaf) {
        final NodeID[] extendedPath;
        // TODO (FRa) : (FRa): add handling for abs/rel path
        if (leaf != null) {
            extendedPath = new NodeID[path.getSteps().length + 1];
            System.arraycopy(path.getSteps(), 0, extendedPath, 0, path.getSteps().length);
            extendedPath[extendedPath.length-1] = leaf.getNodeID();
        } else {
            extendedPath = path.getSteps();
        }

        return extendedPath;
    }
}
