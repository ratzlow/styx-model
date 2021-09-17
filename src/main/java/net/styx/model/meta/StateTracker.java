package net.styx.model.meta;

import java.util.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

// todo: can we prevent static fqPath to be re-instantiated as it will never change?
// todo: add root ID of graph to tracker to manifest "owner"
// todo: avoid creation of path during call chain
public class StateTracker {
    /**
     * Anonymous NodeID to form the lower bound in a tree.
     */
    private static final NodeDef<?> ANY_LOWER_BOUND = new Any(0, "*");

    /**
     * Anonymous NodeID to form the upper bound in a tree.
     */
    private static final NodeDef<?> ANY_UPPER_BOUND = new Any(Integer.MAX_VALUE, "*");


    private final List<ChangeOp<?>> changeLog = new ArrayList<>();
    private final NavigableMap<NodePath<?>, Object> live = new TreeMap<>();


    /**
     * @return all nodes of object tree flatten out to K=path to Node and V=the actual Node
     */
    public NavigableMap<NodePath<?>, Object> getNodes() {
        return Collections.unmodifiableNavigableMap(live);
    }

    public int changeCount() {
        return changeLog.size();
    }

    /**
     * Copy all values from source into this one.
     *
     * @param source container to copy state from into this tracker
     * @return updated instance with entries from source under new fqPath
     */
    public StateTracker load(NodePath<?> newPrefix, NodePath<?> current, StateTracker source) {
        for (var entry : source.live.entrySet()) {
            NodePath<?> existing = entry.getKey();
            NodePath<?> sub = existing.lowerThan(current);
            NodePath<?> fqPath = NodePath.combine(newPrefix, sub);
            set(fqPath, entry.getValue());
        }

        return this;
    }


    /**
     * Move data of subtree identified by #headNodePath out of current state into new tracker.
     * @param newPrefix new prefix (upper path down to current inclusive)
     * @param current path to node to handle
     * @return new instance initialized with state of current's children.
     */
    public StateTracker unload(NodePath<?> newPrefix, NodePath<?> current) {
        StateTracker extract = new StateTracker();

        // find all elements of subtree (children)
        NodePath<?> lower = new NodePath<>(current, new NodeID<>(ANY_LOWER_BOUND));
        NodePath<?> upper = new NodePath<>(current, new NodeID<>(ANY_UPPER_BOUND));
        Map<NodePath<?>, Object> deleteCandidates = new TreeMap<>(live.subMap(lower, upper));
        for (var entry : deleteCandidates.entrySet()) {
            NodePath<?> existingValue = entry.getKey();
            // rm from current state
            set(existingValue, null);

            // mv into independent capsule
            NodePath<?> sub = existingValue.lowerThan(current);
            NodePath<?> fqPath = NodePath.combine(newPrefix, sub);
            Object value = entry.getValue();
            extract.set(fqPath, value);
        }

        // delete current element
        Object currentValue = getInternal(current);
        set(current, null);
        extract.set(newPrefix, currentValue);

        return extract;
    }

    /**
     * Retrieve or create+store on demand.
     */
    public <E, T extends NodeDef<E>> E get(NodePath<?> path, NodeID<T> nodeID, Function<NodePath<T>, E> factory) {
        NodePath<T> fqPath = fqPath(path, nodeID);
        E current = getInternal(fqPath);

        if (current == null) {
            current = factory.apply(fqPath);
            set(fqPath, current);
        }

        return current;
    }

    public <E, T extends NodeDef<E>> E get(NodePath<?> path, NodeID<T> nodeID) {
        NodePath<T> fqPath = fqPath(path, nodeID);
        return getInternal(fqPath);
    }

    @SuppressWarnings("unchecked")
    private <E> E getInternal(NodePath<?> fqPath) {
        return (E) live.get(fqPath);
    }

    public <E, T extends NodeDef<E>> void set(NodePath<?> path, NodeID<T> nodeID, E value) {
        set(fqPath(path, nodeID), value);
    }

    public <E> void set(NodePath<?> fqPath, E value) {
        ChangeOp<E> op = null;
        // rm node from graph
        if (value == null) {
            @SuppressWarnings("unchecked")
            E before = (E) live.remove(fqPath);
            // no point in saving an idempotent operation
            if (before != null) {
                op = new ChangeOp<>(ChangeOp.Type.REMOVE, fqPath, before, null);
            }
        } else {
            @SuppressWarnings("unchecked")
            NodeDef<E> def = (NodeDef<E>) fqPath.getLeaf().def();
            E after = def.normalize(value);

            @SuppressWarnings("unchecked")
            E before = (E) live.put(fqPath, after);
            if (before != value) {
                op = new ChangeOp<>(ChangeOp.Type.SET, fqPath, before, after);
            }
        }

        if (op != null) {
            changeLog.add(op);
        }
    }

    private <E, T extends NodeDef<E>> NodePath<T> fqPath(NodePath<?> path, NodeID<T> nodeID) {
        return new NodePath<>(requireNonNull(path, "path"), requireNonNull(nodeID, "nodeID"));
    }


    //------------------------------------------------------------------------------------------------------------------
    // inner classes
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Unnamed generic def
     */
    private static final class Any implements NodeDef<Object> {
        private final int id;
        private final String name;

        public Any(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public String getDefaultName() {
            return name;
        }
    }
}
