package net.styx.model.tree.traverse;

import net.styx.model.meta.NodeID;

import java.util.Iterator;
import java.util.List;

public class Step implements Iterable<Step> {

    NodeID nodeID;
    Operation operation = Operation.UNCHANGED;
    List<Step> children;




    @Override
    public Iterator<Step> iterator() {
        return children.iterator();
    }
}
