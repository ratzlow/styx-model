package net.styx.model.sample.meta;

import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.meta.AttrDef;
import net.styx.model.meta.ComponentDef;
import net.styx.model.meta.NodeID;
import net.styx.model.sample.Address;

public class AddressDef extends ComponentDef<Address> {
    public static final AddressDef INSTANCE = new AddressDef();

    private final NodeID<AttrDef<String>> street = new NodeID<>(Dictionary.STREET);
    private final NodeID<AttrDef<Integer>> zip = new NodeID<>(Dictionary.ZIP);

    AddressDef() {
        super(2, "address");
    }

    public NodeID<AttrDef<String>> street() {
        return street;
    }
    public NodeID<AttrDef<Integer>> zip() {return zip;}

    public Address create(NodePath<AddressDef> path, StateTracker tracker) {
        return new Address(path, tracker);
    }
}