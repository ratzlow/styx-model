package net.styx.model.sample;

import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.sample.meta.AddressType;

public class Address {
    private static final AddressType DEF = AddressType.INSTANCE;
    private final NodePath<AddressType> path;
    private final StateTracker tracker;

    public Address(NodePath<AddressType> path, StateTracker stateTracker) {
        this.path = path;
        this.tracker = stateTracker;
    }

    public String getStreet() {
        return tracker.get(path, DEF.street());
    }

    public void setStreet(String street) {
        tracker.set(path, DEF.street(), street);
    }

    public Integer getZip() {
        return tracker.get(path, DEF.zip());
    }

    public void setZip(Integer zip) {
        tracker.set(path, DEF.zip(), zip);
    }

    @Override
    public String toString() {
        return "Address{path='" + path + '}';
    }
}
