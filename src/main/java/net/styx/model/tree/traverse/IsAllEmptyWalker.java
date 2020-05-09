package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.function.Predicate;

/**
 * Test first on root node before listener is called for children to ensure testing from top->bottom
 */
public class IsAllEmptyWalker implements TreeWalker {
    private final Predicate<Node> isEmpty = Stateful::isEmpty;

    private Node notEmptyNode;


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
        boolean isEmpty = notEmptyNode != null;
        if (isEmpty) {
            System.out.println("First non-empty node detected: " + notEmptyNode.getNodeID());
        }

        return isEmpty;
    }

    private void testIsChanged(Node node) {
        if (!isEmpty.test(node)) {
            notEmptyNode = node;
        }
    }
}
