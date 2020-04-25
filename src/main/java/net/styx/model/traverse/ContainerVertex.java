package net.styx.model.traverse;

import net.styx.model.meta.DataType;
import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Group;
import net.styx.model.tree.Leaf;
import net.styx.model.tree.Node;

public class ContainerVertex implements Vertex {

    private final Node parent;

    public ContainerVertex(Node parent) {
        this.parent = parent;
    }

    @Override
    public Vertex getChild(Step toChild, NodeFactory nodeFactory) {
        final Vertex child;
        Descriptor childDescriptor = toChild.getDescriptor();
        DataType dataType = childDescriptor.getDataType();
        if (dataType == DataType.COMPONENT) {
            Node node = parent.getNode(childDescriptor);
            if (node == null) {
                node = nodeFactory.createDataContainer();
                parent.setNode(node);
            }
            child = new ContainerVertex(node);

        } else if (dataType == DataType.GROUP) {
            Group<?, ?> group = parent.getGroup(childDescriptor);
            child = new GroupVertex(group);

        } else {
            Leaf existing = parent.getLeaf(childDescriptor);
            Leaf newValue = nodeFactory.createLeaf();
            Leaf resultingLeaf = merge(existing, newValue);
            parent.setLeaf(resultingLeaf);
            child = new LeafVertex(existing);
        }

        return child;
    }


    @Override
    public Vertex getChild(Step toChild) {
        final Vertex child;
        Descriptor childDescriptor = toChild.getDescriptor();
        DataType dataType = childDescriptor.getDataType();
        if (dataType == DataType.COMPONENT) {
            Node node = parent.getNode(childDescriptor);
            child = node != null ? new ContainerVertex(node) : null;

        } else if (dataType == DataType.GROUP) {
            throw new IllegalArgumentException("GROUP not yet supported!");

        } else {
            Leaf leaf = parent.getLeaf(childDescriptor);
            child = leaf != null ? new LeafVertex(leaf) : null;
        }

        return child;
    }


    @Override
    public void removeChild(Step toChild) {
        parent.remove(toChild.getDescriptor());
    }

    @Override
    public Descriptor getDescriptor() {
        return parent.getDescriptor();
    }

    private static Leaf merge(Leaf existing, Leaf newVal) {
        final Leaf merged;
        if (existing != null && newVal != null) {
            existing.setValueLeaf(newVal);
            merged = existing;

        } else if (existing == null) {
            merged = newVal;

        } else {
            merged = existing;
        }

        return merged;
    }
}
