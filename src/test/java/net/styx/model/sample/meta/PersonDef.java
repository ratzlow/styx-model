package net.styx.model.sample.meta;

import net.styx.model.meta.*;
import net.styx.model.sample.Book;
import net.styx.model.sample.Person;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class PersonDef extends ComponentDef<Person> {
    public static final PersonDef INSTANCE = new PersonDef();
    public static final NodePath<PersonDef> ROOT_ID = new NodePath<>(new NodeID<>(INSTANCE));

    // attributes
    private final NodeID<NodeDef<String>> name = new NodeID<>(Dictionary.NAME);
    private final NodeID<NodeDef<LocalDateTime>> birthday = new NodeID<>(Dictionary.BIRTHDAY);
    private final NodeID<NodeDef<List<String>>> accounts = new NodeID<>(Dictionary.ACCOUNTS);


    // components
    private final NodeID<AddressDef> work = new NodeID<>(1, "work", AddressDef.INSTANCE);
    private final NodeID<AddressDef> home = new NodeID<>(2, "home", AddressDef.INSTANCE);

    // groups
    private final NodeID<GroupDef<Book, Collection<Book>, BookDef>> books = new NodeID<>(new GroupDef<>(7, "books", BookDef.INSTANCE));

    PersonDef() {
        super(1, "person");
    }

    public NodeID<NodeDef<String>> name() {
        return name;
    }

    public NodeID<NodeDef<LocalDateTime>> birthday() {return birthday;}

    public NodeID<NodeDef<List<String>>> accounts() {return accounts; }

    public NodeID<AddressDef> work() {
        return work;
    }

    public NodeID<AddressDef> home() {
        return home;
    }

    public NodeID<GroupDef<Book, Collection<Book>, BookDef>> books() {
        return books;
    }

    public Person create(StateTracker tracker) {
        return create(ROOT_ID, tracker);
    }

    public Person create(NodePath<PersonDef> path, StateTracker tracker) {
        return new Person(path, tracker);
    }
}
