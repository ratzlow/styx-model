package net.styx.model.sample;

import net.styx.model.meta.*;
import net.styx.model.sample.meta.Dictionary;

public class Address {
    private final NodePath<Type> path;
    private final StateTracker tracker;

    public Address(NodePath<Type> path, StateTracker stateTracker) {
        this.path = path;
        this.tracker = stateTracker;
    }

    public String getStreet() {
        return tracker.get(path, Type.INSTANCE.street);
    }

    public void setStreet(String street) {
        tracker.set(path, Type.INSTANCE.street, street);
    }

    public Integer getZip() {
        return tracker.get(path, Type.INSTANCE.zip);
    }

    public void setZip(Integer zip) {
        tracker.set(path, Type.INSTANCE.zip, zip);
    }

    @Override
    public String toString() {
        return "Address{path='" + path + '}';
    }

    public static class Type extends ComponentType<Address> {
        public static final Type INSTANCE = new Type();

        private final NodeID<NodeType<String>> street = new NodeID<>(Dictionary.STREET);
        private final NodeID<NodeType<Integer>> zip = new NodeID<>(Dictionary.ZIP);

        Type() {
            super(2, "address");
        }
    }
}
