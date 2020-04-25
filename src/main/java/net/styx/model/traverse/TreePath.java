package net.styx.model.traverse;

import net.styx.model.meta.Descriptor;

import java.util.ArrayList;
import java.util.List;

public class TreePath {
    enum Type {ABSOLUTE, RELATIVE}


    final Type pathType;
    final Step[] steps;

    private TreePath(Type pathType, Step[] steps) {
        this.pathType = pathType;
        this.steps = steps;
    }

    public Type getPathType() {
        return pathType;
    }

    public Step[] getSteps() {
        return steps;
    }

    static class Builder {
        final List<Step> steps = new ArrayList<>();
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

        Builder add(Descriptor ... descriptors) {
            for (Descriptor descriptor : descriptors) {
                steps.add(new Step(descriptor));
            }
            return this;
        }

        Builder add(Descriptor descriptor, Object key) {
            steps.add(new Step(descriptor, key));
            return this;
        }

        TreePath build() {
            return new TreePath(pathType, steps.toArray(new Step[steps.size()]));
        }
    }
}
