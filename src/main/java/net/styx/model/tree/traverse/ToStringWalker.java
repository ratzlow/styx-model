package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;
import net.styx.model.tree.*;

import java.util.ArrayList;
import java.util.Collection;

public class ToStringWalker implements TreeWalker {
    // TODO (FRa) : (FRa): should be more light weight e.g. string builder with
    //  stack control var e.g. bracket counter
    private final Collection<String> events = new ArrayList<>();

    @Override
    public void onEnter(Leaf leaf) {
        events.add(String.format("{ %s='%s'", format(leaf.getNodeID()), leaf.toString()));
    }

    @Override
    public void onEnter(Container container) {
        onEnterNode(container);
    }

    @Override
    public void onEnter(Group<?> group) {
        onEnterNode(group);
    }

    @Override
    public void onExit(NodeID nodeID) {
        events.add("}");
    }

    @Override
    public String toString() {
        return String.join(" ", events);
    }

    private String format(NodeID id) {
        String s = "{ " + id.getDescriptor().name().toLowerCase();
        if (id.getIdx() != NodeID.NO_IDX) {
            s += "[" + id.getIdx() + "]=";
        }
        return s;
    }

    private void onEnterNode(Node node) {
        events.add(format(node.getNodeID()));
    }
}
