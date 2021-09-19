package net.styx.model.sample;

import net.styx.model.meta.*;
import net.styx.model.sample.meta.Dictionary;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Person {
    private final NodePath<Type> path;
    private final StateTracker tracker;

    public Person(NodePath<Type> path, StateTracker stateTracker) {
        this.path = path;
        this.tracker = stateTracker;
    }
    
    public String getName() {
        return tracker.get(path, Type.INSTANCE.name);
    }

    public void setName(String name) {
        tracker.set(path, Type.INSTANCE.name, name);
    }

    public void setAccounts(List<String> accounts) {
        tracker.set(path, Type.INSTANCE.accounts, accounts);
    }

    public List<String> getAccounts() {
        return tracker.get(path, Type.INSTANCE.accounts);
    }

    public LocalDateTime getBirthday() {
        return tracker.get(path, Type.INSTANCE.birthday);
    }

    public void setBirthday(LocalDateTime birthday) {
        tracker.set(path, Type.INSTANCE.birthday, birthday);
    }

    public Address getHome() {
        return tracker.get(path, Type.INSTANCE.home);
    }

    public Address home() {
        return tracker.get(path, Type.INSTANCE.home, fqPath -> new Address(fqPath, tracker));
    }

    public void setHome(Address home) {
        tracker.set(path, Type.INSTANCE.home, home);
    }

    public Address getWork() {
        return tracker.get(path, Type.INSTANCE.work);
    }

    public Address work() {
        return tracker.get(path, Type.INSTANCE.work, fqPath -> new Address(fqPath, tracker));
    }

    public void setWork(Address work) {
        tracker.set(path, Type.INSTANCE.work, work);
    }

    public Collection<Book> getBooks() {
        return tracker.get(path, Type.INSTANCE.books);
    }

    public Collection<Book> books() {
        return tracker.get(path, Type.INSTANCE.books, fqPath -> new Group<>(fqPath, tracker));
    }

    public void setBooks(Collection<Book> books) {
        tracker.set(path, Type.INSTANCE.books, books);
    }

    @Override
    public String toString() {
        return "Person{path='" + path + '}';
    }

    //------------------------------------------------------------------------------------------------------------------

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
        private final NodeID<GroupType<Book, Collection<Book>, Book.Type>> books = new NodeID<>(new GroupType<>(7, "books", Book.Type.INSTANCE));

        Type() {
            super(1, "person");
        }

        public Person create(StateTracker tracker) {
            return create(ROOT_ID, tracker);
        }

        public Person create(NodePath<Type> path, StateTracker tracker) {
            return new Person(path, tracker);
        }
    }
}
