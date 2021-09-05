package net.styx.model_v1.tree.traverse;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.*;

import java.util.function.Predicate;

/**
 * Test first on root node before listener is called for children to ensure testing from top->bottom
 */
public class IsAllEmptyWalker implements TreeWalker {
    private final Predicate<StatefulNode> isEmpty = StatefulNode::isEmpty;

    private Node<?> initializedNode;


    @Override
    public void onEnter(Leaf leaf) {
        testIsChanged(leaf);
    }

    @Override
    public void onEnter(Container container) {
        testIsChanged(container);
    }

    @Override
    public void onEnter(Group<?> group) {
        testIsChanged(group);
    }

    @Override
    public void onExit(NodeID nodeID) {
    }

    @Override
    public boolean proceed() {
        boolean isEmpty = initializedNode != null;
        if (isEmpty) {
            System.out.println("First non-empty node detected: " + initializedNode.getNodeID());
        }

        return isEmpty;
    }

    private void testIsChanged(StatefulNode node) {
        if (!isEmpty.test(node)) {
            initializedNode = node;
        }
    }
}
