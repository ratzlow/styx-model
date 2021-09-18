package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Book;
import net.styx.model.sample.Person;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class PersonType extends ComponentType<Person> {
    public static final PersonType INSTANCE = new PersonType();
    public static final NodePath<PersonType> ROOT_ID = new NodePath<>(new NodeID<>(INSTANCE));

    // attributes
    private final NodeID<NodeType<String>> name = new NodeID<>(Dictionary.NAME);
    private final NodeID<NodeType<LocalDateTime>> birthday = new NodeID<>(Dictionary.BIRTHDAY);
    private final NodeID<NodeType<List<String>>> accounts = new NodeID<>(Dictionary.ACCOUNTS);


    // components
    private final NodeID<AddressType> work = new NodeID<>(1, "work", AddressType.INSTANCE);
    private final NodeID<AddressType> home = new NodeID<>(2, "home", AddressType.INSTANCE);

    // groups
    private final NodeID<GroupType<Book, Collection<Book>, BookType>> books = new NodeID<>(new GroupType<>(7, "books", BookType.INSTANCE));

    PersonType() {
        super(1, "person");
    }

    public NodeID<NodeType<String>> name() {
        return name;
    }

    public NodeID<NodeType<LocalDateTime>> birthday() {return birthday;}

    public NodeID<NodeType<List<String>>> accounts() {return accounts; }

    public NodeID<AddressType> work() {
        return work;
    }

    public NodeID<AddressType> home() {
        return home;
    }

    public NodeID<GroupType<Book, Collection<Book>, BookType>> books() {
        return books;
    }

    public Person create(StateTracker tracker) {
        return create(ROOT_ID, tracker);
    }

    public Person create(NodePath<PersonType> path, StateTracker tracker) {
        return new Person(path, tracker);
    }
}
