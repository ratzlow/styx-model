package net.styx.model_v1.tree;

import net.styx.model_v1.meta.NodeID;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class MapStore<E extends Stateful> implements Stateful {

    private boolean created = true;

    /**
     * Elements are mutable but will remain in collection. Access to previous element state
     * possible available via Node API
     */
    private Map<NodeID, E> backup;
    private final Map<NodeID, E> live;

    public MapStore(Map<NodeID, E> live) {
        this.live = live;
    }

    public Map<NodeID, E> getLive() {
        return live;
    }

    @Override
    public boolean isEmpty() {
        return live.isEmpty();
    }

    /**
     * @return true ... if newly created and attributes set OR existing content was modified
     */
    @Override
    public boolean isChanged() {
        // newly created container with initial elements
        boolean justCreated = created && backup == null && !live.isEmpty();

        // modified container
        boolean modified = backup != null && !backup.keySet().equals(live.keySet());

        return justCreated || modified;
    }

    @Override
    public void commit() {
        live.values().forEach(Stateful::commit);
        backup = null;
        created = false;
    }

    @Override
    public void rollback() {
        // ensure the same elements are restored
        if (backup != null) {
            live.clear();
            live.putAll(backup);
            backup = null;
        }
        live.values().forEach(Stateful::rollback);
        created = false;
    }

    boolean remove(NodeID nodeID) {
        checkBackup();
        E removed = live.remove(nodeID);
        return removed != null;
    }

    void checkBackup() {
        if (backup == null) {
            backup = Map.copyOf(live);
        }
    }

    //--------------------------------------------------------------------------------------
    // Object overrides
    //--------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringJoiner(", ", MapStore.class.getSimpleName() + "[", "]")
                .add("created=" + created)
                .add("backup=" + backup)
                .add("live=" + live)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapStore<?> mapStore = (MapStore<?>) o;
        return created == mapStore.created &&
                Objects.equals(backup, mapStore.backup) &&
                live.equals(mapStore.live);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, backup, live);
    }
}
