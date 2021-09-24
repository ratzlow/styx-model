package net.styx.model.sample;

import net.styx.model.meta.*;

import java.util.Collection;

public class Job implements Node<Job.Type> {
    private final GenericNode<Job.Type> mixin;

    //------------------------------------------- Constructors ---------------------------------------------------------

    public Job(StateTracker stateTracker, NodePath<Type> path) {
        this.mixin = new GenericNode<>(stateTracker, path);
    }

    public Job(StateTracker stateTracker) {
        this(stateTracker, Type.DEFAULT_PATH);
    }

    public Job() {
        this(new StateTracker(), Type.DEFAULT_PATH);
    }

    //------------------------------------------- Properties -----------------------------------------------------------

    public String getDescription() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.description);
    }

    public void setDescription(String description) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.description, description);
    }

    public Money getIncome() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.income);
    }

    public void setIncome(Money income) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.income, income);
    }

    public Collection<Project> getAssignments() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.assignments);
    }

    public Collection<Project> assignments() {
        // TODO: can we hide known params behind mixin API? Here Group: only NodeID needed!, NodePath in all cases known to GenNode
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.assignments, fqPath -> new Group<>(fqPath, mixin.tracker()));
    }

    // TODO: test assignments node is merged into tree and unmerged when set to null
    public void setAssignments(Collection<Project> assignments) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.assignments, assignments);
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

    public static class Type extends ComponentType<Job> {
        public static final Type INSTANCE = new Type();
        public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));

        private final NodeID<NodeType<String>> description = new NodeID<>(Dictionary.DESCRIPTION);
        private final NodeID<NodeType<Money>> income = new NodeID<>(Dictionary.INCOME);
        // groups
        private final NodeID<GroupType<Project, Collection<Project>, Project.Type>> assignments =
                new NodeID<>(new GroupType<>(72, "assignments", Project.Type.INSTANCE));


        Type() {
            super(3, "job");
        }
    }
}
