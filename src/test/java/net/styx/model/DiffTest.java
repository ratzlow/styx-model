package net.styx.model;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class DiffTest {

    @Test
    void leaf() {
        Leaf leaf = new StringLeaf(Descriptor.NAME, "Frank");
        DiffWalker walker = new DiffWalker();
        leaf.accept(walker);
        NodeDiff diff = walker.getNodeDiff();
        assertThat(diff.nodeID).isEqualTo(leaf.getNodeID());
        assertThat(diff.operation).isEqualTo(NodeDiff.Operation.SET);
    }


    static class DiffWalker implements TreeWalker {
        NodeDiff diff;

        @Override
        public void onEnter(Leaf leaf) {
            if (!leaf.isChanged()) return;

            NodeDiff.Operation op = leaf.isEmpty() ? NodeDiff.Operation.DELETE : NodeDiff.Operation.SET;
            add(leaf, op);
        }

        @Override
        public void onEnter(Container container) {

        }

        @Override
        public void onEnter(Group<?> group) {

        }

        @Override
        public void onExit(NodeID nodeID) {

        }

        NodeDiff getNodeDiff() {
            return diff;
        }

        private void add(Node node, NodeDiff.Operation op) {
            if (diff == null) {
                diff = new NodeDiff(node.getNodeID(), op);
            }
        }
    }

    static class NodeDiff {
        enum Operation { SET, DELETE, UNCHANGED }

        NodeID nodeID;
        Operation operation;

        Collection<NodeDiff> children = new ArrayList<>();

        public NodeDiff(NodeID nodeID, Operation operation) {
            this.nodeID = nodeID;
            this.operation = operation;
        }
    }
}
