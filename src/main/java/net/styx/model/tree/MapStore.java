package net.styx.model.tree;

import net.styx.model.meta.NodeID;

import java.util.Map;

// TODO (FRa) : (FRa): remove lazy init to simplify code
public class MapStore<E extends Node> implements Stateful {

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
        Node removed = live.remove(nodeID);
        return removed != null;
    }

    void checkBackup() {
        if (backup == null) {
            backup = Map.copyOf(live);
        }
    }
}
