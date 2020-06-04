package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Test first on root node before listener is called for children to ensure testing from top->bottom
 */
public class IsAnyChangedWalker implements TreeWalker {
    private static final Logger LOGGER = Logger.getLogger(IsAllEmptyWalker.class.getName());

    private final Predicate<StatefulNode> isChanged = StatefulNode::isChanged;

    private Node<?> changedNode;

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


    private void testIsChanged(StatefulNode node) {
        if (isChanged.test(node)) {
            LOGGER.warning("Changed node detected: " + node.toString());
            changedNode = node;
        }
    }
}
