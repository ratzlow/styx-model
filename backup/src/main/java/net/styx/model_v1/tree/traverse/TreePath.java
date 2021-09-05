package net.styx.model_v1.tree.traverse;

import net.styx.model_v1.meta.Descriptor;
import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.IdxNodeID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreePath {
    enum Type {ABSOLUTE, RELATIVE}


    final Type pathType;
    final NodeID[] steps;

    private TreePath(Type pathType, NodeID[] steps) {
        this.pathType = pathType;
        this.steps = steps;
    }

    public Type getPathType() {
        return pathType;
    }

    public NodeID[] getSteps() {
        return steps;
    }

    static class Builder {
        final List<NodeID> steps = new ArrayList<>();
        final Type pathType;

        public Builder(Type pathType) {
            this.pathType = pathType;
        }

        static Builder relative() {
            return new Builder(Type.RELATIVE);
        }

        static Builder absolute() {
            return new Builder(Type.ABSOLUTE);
        }

        Builder add(NodeID ... nodeIDs) {
            steps.addAll(Arrays.asList(nodeIDs));
            return this;
        }

        Builder add(Descriptor descriptor, long idx) {
            steps.add(new IdxNodeID(descriptor, idx));
            return this;
        }

        TreePath build() {
            return new TreePath(pathType, steps.toArray(new NodeID[0]));
        }
    }
}
