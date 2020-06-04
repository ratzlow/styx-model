package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.meta.NodeType;
import net.styx.model.tree.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class ImmutabilityWalker implements TreeWalker {
    private static final Logger LOGGER = Logger.getLogger(ImmutabilityWalker.class.getCanonicalName());

    private final Deque<StatefulNode> visitedNodes = new ArrayDeque<>();

    @Override
    public void onEnter(Leaf leaf) {
        LOGGER.info(() -> "Enter leaf=" + leaf.getNodeID());
        visitedNodes.push(new ImmutableLeaf(leaf));
    }

    @Override
    public void onEnter(Container container) {
        LOGGER.info(() -> "Enter container=" + container.getNodeID());
        visitedNodes.push(container);
    }

    @Override
    public void onEnter(Group<?> group) {
        LOGGER.info(() -> "Enter group=" + group.getNodeID());

        visitedNodes.push(group);
    }

    @Override
    public void onExit(NodeID nodeID) {
        LOGGER.info(() -> "Exit nodeID=" + nodeID);

        NodeType nodeType = nodeID.getDescriptor().getNodeType();

        if (nodeType == NodeType.GROUP) {
            replaceSubTree(nodeID, this::immutableGroup);
        } else if (nodeType == NodeType.CONTAINER) {
            replaceSubTree(nodeID, this::immutableContainer);
        }
    }

    public StatefulNode getImmutableNode() {
        if (visitedNodes.size() != 1) {
            String msg = "Stream of nodes could not be reduced to tree with single root node";
            throw new IllegalStateException(msg);
        }
        return visitedNodes.pop();
    }


    private StatefulNode immutableGroup(StatefulNode group, Collection<StatefulNode> children) {
        return new ImmutableGroup<>((Group<StatefulNode>) group, children);
    }

    private StatefulNode immutableContainer(StatefulNode container, Collection<StatefulNode> children) {
        Container immutableContainer = new ImmutableContainer((Container) container, children);
        return container.getNodeID().getDescriptor().getDomainModelFactory().apply(immutableContainer);
    }

    /**
     * Pop all nodes from stack until element with nodeID was added.
     * Means all popped elements are children of it.
     * Now create immutable version of popped mutable parent with all other elements being
     * immutable children.
     * Add wrapped parent again on stack until it is popped by it's parent.
     *
     * @param nodeID of formerly added parent
     * @param immutableNodeGenerator to wrap mutable parent and immutable children
     */
    // TODO (FRa) : (FRa): perf needs to touch each node with push, peek, pop, push;
    //  should be more efficient with simple recursion
    private void replaceSubTree(NodeID nodeID,
                                BiFunction<StatefulNode, Collection<StatefulNode>, StatefulNode> immutableNodeGenerator) {

        Collection<StatefulNode> immutableChildren = new ArrayDeque<>();
        StatefulNode parent = null;
        while (parent == null) {
            StatefulNode top = visitedNodes.pop();
            if (top.getNodeID().equals(nodeID)) {
                parent = top;
            } else {
                immutableChildren.add(top);
            }
        }

        StatefulNode immutableParent = immutableNodeGenerator.apply(parent, immutableChildren);

        visitedNodes.push(immutableParent);
    }
}
