package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Address;

public class AddressType extends ComponentType<Address> {
    public static final AddressType INSTANCE = new AddressType();

    private final NodeID<NodeType<String>> street = new NodeID<>(Dictionary.STREET);
    private final NodeID<NodeType<Integer>> zip = new NodeID<>(Dictionary.ZIP);

    AddressType() {
        super(2, "address");
    }

    public NodeID<NodeType<String>> street() {
        return street;
    }
    public NodeID<NodeType<Integer>> zip() {return zip;}

    public Address create(NodePath<AddressType> path, StateTracker tracker) {
        return new Address(path, tracker);
    }
}