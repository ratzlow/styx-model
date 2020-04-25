package net.styx.model.traverse;

import net.styx.model.meta.DataType;
import net.styx.model.meta.Descriptor;
import net.styx.model.tree.Group;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Node;
import net.styx.model.tree.Stateful;

public class GroupVertex implements Vertex {

    private final Group parent;

    public GroupVertex(Group parent) {
        this.parent = parent;
    }

    @Override
    public Vertex getChild(Step childStep) {
        Object key = childStep.getGroupElemKey().orElseThrow();
        Stateful element = parent.get(key);
        Descriptor childDescriptor = childStep.getDescriptor();
        DataType dataType = childDescriptor.getDataType();
        final Vertex child;

        if (element == null) {
            child = null;
        } else if (dataType == DataType.COMPONENT) {
            child = new ContainerVertex((Node) element);
        } else {
            throw new UnsupportedOperationException("");
        }

        return child;
    }

    @Override
    public Vertex getChild(Step childStep, NodeFactory nodeFactory) {
        Vertex childVertex = getChild(childStep);

        if (childVertex == null) {
            childVertex = new ContainerVertex(nodeFactory.createIdentifiableDataContainer());

        } else {
            throw new UnsupportedOperationException();
        }

        return childVertex;
    }

    @Override
    public void removeChild(Step child) {
        throw new UnsupportedOperationException("No getChild from Leaf as parent possible!");
    }

    @Override
    public Descriptor getDescriptor() {
        return parent.getDescriptor();
    }
}
