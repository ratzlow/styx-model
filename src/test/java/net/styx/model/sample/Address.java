package net.styx.model.sample;

import net.styx.model.changelog.NodePath;
import net.styx.model.changelog.StateTracker;
import net.styx.model.sample.meta.AddressDef;

public class Address {
    private final AddressDef def;
    private final NodePath<AddressDef> path;
    private final StateTracker tracker;

    public Address(NodePath<AddressDef> path, StateTracker stateTracker) {
        this.path = path;
        this.def = path.getLeaf().def();
        this.tracker = stateTracker;
    }

    String getStreet() {
        return tracker.get(path, def.street());
    }

    void setStreet(String street) {
        tracker.set(path, def.street(), street);
    }
}
