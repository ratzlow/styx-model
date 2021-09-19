package net.styx.model.sample;

import net.styx.model.meta.*;

public class Address implements Node<Address.Type> {
    private final NodeMixin<Address.Type> mixin;

    public Address(NodePath<Type> path, StateTracker stateTracker) {
        this.mixin = new NodeMixin<>(stateTracker, path);
    }

    public String getStreet() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.street);
    }

    public void setStreet(String street) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.street, street);
    }

    public Integer getZip() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.zip);
    }

    public void setZip(Integer zip) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.zip, zip);
    }

    //------------------------------------------- NodeMixin API --------------------------------------------------------

    @Override
    public NodePath<Address.Type> getNodePath() {
        return mixin.getNodePath();
    }

    @Override
    public void connect(NodePath<Address.Type> prefix, StateTracker stateTracker) {
        mixin.connect(prefix, stateTracker);
    }

    @Override
    public void disconnect() {
        mixin.disconnect();
    }

    @Override
    public String toString() {
        return mixin.toString();
    }

    //------------------------------------------- Meta -----------------------------------------------------------------

    public static class Type extends ComponentType<Address> {
        public static final Type INSTANCE = new Type();

        private final NodeID<NodeType<String>> street = new NodeID<>(Dictionary.STREET);
        private final NodeID<NodeType<Integer>> zip = new NodeID<>(Dictionary.ZIP);

        Type() {
            super(2, "address");
        }
    }
}
