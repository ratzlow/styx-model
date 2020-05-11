package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.function.Predicate;

/**
 * Test first on root node before listener is called for children to ensure testing from top->bottom
 */
public class IsAnyChangedWalker implements TreeWalker {
    private final Predicate<Node> isChanged = Stateful::isChanged;

    private Node changedNode;

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

    /**
     * @return true ... as long as _no_ changed node was detected
     */
    @Override
    public boolean proceed() {
        return changedNode == null;
    }


    private void testIsChanged(Node node) {
        if (isChanged.test(node)) {
            System.out.println("Changed node detected: " + node.getNodeID());
            changedNode = node;
        }
    }
}