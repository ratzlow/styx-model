package net.styx.model.changelog;

import net.styx.model.meta.NodeDef;
import net.styx.model.meta.NodeID;

import java.util.*;

import static java.util.Objects.requireNonNull;

// todo: can we prevent static fqPath to be reinstantiated as it will never change?
public class StateTracker {
    final List<ChangeOp<?>> changeLog = new ArrayList<>();
    final Map<NodePath, Object> live = new HashMap<>();

    public <E, T extends NodeDef<E>> E get(NodePath<?> path, NodeID<T> nodeID) {
        NodePath<T> fqPath = fqPath(path, nodeID);
        @SuppressWarnings("unchecked")
        E current = (E) live.get(fqPath);

        return current;
    }

    public <E, T extends NodeDef<E>> void set(NodePath<?> path, NodeID<T> nodeID, E value) {
        NodePath<T> fqPath = fqPath(path, nodeID);
        ChangeOp<E> op = null;
        // rm node from graph
        if (value == null) {
            @SuppressWarnings("unchecked")
            E before = (E) live.remove(fqPath);
            // no point in saving an idempotent operation
            if (before != null) {
                op = new ChangeOp<>(ChangeOp.Type.REMOVE, path, before, null);
            }
        } else {
            @SuppressWarnings("unchecked")
            E before = (E) live.put(fqPath, value);
            if (before != value) {
                op = new ChangeOp<>(ChangeOp.Type.SET, path, before, value);
            }
        }

        if (op != null) {
            changeLog.add(op);
        }
    }


    private <E, T extends NodeDef<E>> NodePath<T> fqPath(NodePath<?> path, NodeID<T> nodeID) {
        return new NodePath<T>(requireNonNull(path, "path"), requireNonNull(nodeID, "nodeID"));
    }

}
