package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Address;

public class AddressDef extends ComponentDef<Address> {
    public static final AddressDef INSTANCE = new AddressDef();

    private final NodeID<NodeDef<String>> street = new NodeID<>(Dictionary.STREET);
    private final NodeID<NodeDef<Integer>> zip = new NodeID<>(Dictionary.ZIP);

    AddressDef() {
        super(2, "address");
    }

    public NodeID<NodeDef<String>> street() {
        return street;
    }
    public NodeID<NodeDef<Integer>> zip() {return zip;}

    public Address create(NodePath<AddressDef> path, StateTracker tracker) {
        return new Address(path, tracker);
    }
}