package net.styx.model.sample;

import net.styx.model.meta.*;

public class Project implements Node<Project.Type> {
    private final GenericNode<Type> mixin;

    //------------------------------------------- Constructors ---------------------------------------------------------

    public Project(NodePath<Project.Type> path, StateTracker stateTracker) {
        this.mixin = new GenericNode<>(stateTracker, path);
    }

    public Project(StateTracker stateTracker) {
        this(Type.DEFAULT_PATH, stateTracker);
    }

    public Project() {
        this(Type.DEFAULT_PATH, new StateTracker());
    }

    //------------------------------------------- Properties -----------------------------------------------------------

    public String getPosition() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.position);
    }

    public void setPosition(String position) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.position, position);
    }

    public int getTeamSize() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.teamSize);
    }

    public void setTeamSize(int teamSize) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.teamSize, teamSize);
    }

    //------------------------------------------- NodeMixin API --------------------------------------------------------

    @Override
    public NodePath<Type> getNodePath() {
        return mixin.getNodePath();
    }

    @Override
    public void connect(NodePath<Type> prefix, StateTracker stateTracker) {
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

    public static class Type extends ComponentType<Project> {
        public static final Type INSTANCE = new Type();
        public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));

        private final NodeID<NodeType<String>> position = new NodeID<>(Dictionary.POSITION);
        private final NodeID<NodeType<Integer>> teamSize = new NodeID<>(Dictionary.TEAM_SIZE);

        Type() {
            super(3, "project");
        }
    }
}
