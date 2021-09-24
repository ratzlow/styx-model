package net.styx.model.sample;

import net.styx.model.meta.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Person implements Node<Person.Type> {
    private final GenericNode<Type> mixin;

    public Person() {
        this(new StateTracker(), Type.ROOT_ID);
    }

    public Person(StateTracker stateTracker, NodePath<Type> path) {
        this.mixin = new GenericNode<>(stateTracker, path);
    }
    
    public Person(StateTracker stateTracker) {
        this(stateTracker, Type.ROOT_ID);
    }

    public String getName() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.name);
    }

    public void setName(String name) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.name, name);
    }

    public void setAccounts(List<String> accounts) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.accounts, accounts);
    }

    public List<String> getAccounts() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.accounts);
    }

    public LocalDateTime getBirthday() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.birthday);
    }

    public void setBirthday(LocalDateTime birthday) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.birthday, birthday);
    }

    public Address getHome() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.home);
    }

    public Address home() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.home, fqPath -> new Address(fqPath, mixin.tracker()));
    }

    public void setHome(Address home) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.home, home);
    }

    public Address getWork() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.work);
    }

    public Address work() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.work, fqPath -> new Address(fqPath, mixin.tracker()));
    }

    public void setWork(Address work) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.work, work);
    }

    public Collection<Book> getBooks() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.books);
    }

    public Collection<Book> books() {
        return mixin.tracker().get(mixin.getNodePath(),  Type.INSTANCE.books, fqPath -> new Group<>(fqPath, mixin.tracker()));
    }

    public void setBooks(Collection<Book> books) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.books, books);
    }

    public Collection<Job> getJobs() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.jobs);
    }

    public Collection<Job> jobs() {
        return mixin.tracker().get(mixin.getNodePath(), Type.INSTANCE.jobs, fqPath -> new Group<>(fqPath, mixin.tracker()));
    }

    public void setJobs(Collection<Job> assignments) {
        mixin.tracker().set(mixin.getNodePath(), Type.INSTANCE.jobs, assignments);
    }

    //------------------------------------------- NodeMixin API --------------------------------------------------------

    @Override
    public NodePath<Person.Type> getNodePath() {
        return mixin.getNodePath();
    }

    @Override
    public void connect(NodePath<Person.Type> prefix, StateTracker stateTracker) {
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

    public static class Type extends ComponentType<Person> {
        public static final Type INSTANCE = new Type();
        public static final NodePath<Type> ROOT_ID = new NodePath<>(new NodeID<>(INSTANCE));

        // attributes
        private final NodeID<NodeType<String>> name = new NodeID<>(Dictionary.NAME);
        private final NodeID<NodeType<LocalDateTime>> birthday = new NodeID<>(Dictionary.BIRTHDAY);
        private final NodeID<NodeType<List<String>>> accounts = new NodeID<>(Dictionary.ACCOUNTS);

        // components
        public final NodeID<Address.Type> work = new NodeID<>(1, "work", Address.Type.INSTANCE);
        public final NodeID<Address.Type> home = new NodeID<>(2, "home", Address.Type.INSTANCE);

        // groups
        private final NodeID<GroupType<Book, Collection<Book>, Book.Type>> books =
                new NodeID<>(new GroupType<>(7, "books", Book.Type.INSTANCE));
        private final NodeID<GroupType<Job, Collection<Job>, Job.Type>> jobs =
                new NodeID<>(new GroupType<>(7, "jobs", Job.Type.INSTANCE));

        Type() {
            super(1, "person");
        }
    }
}
