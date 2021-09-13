package net.styx.model.sample.meta;

import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.meta.AttrDef;
import net.styx.model.meta.ComponentDef;
import net.styx.model.meta.GroupDef;
import net.styx.model.meta.NodeID;
import net.styx.model.sample.Book;
import net.styx.model.sample.Person;

import java.time.LocalDateTime;
import java.util.List;

public class PersonDef extends ComponentDef<Person> {
    public static final PersonDef INSTANCE = new PersonDef();
    public static final NodePath<PersonDef> ROOT_ID = new NodePath<>(new NodeID<>(INSTANCE));

    // attributes
    private final NodeID<AttrDef<String>> name = new NodeID<>(Dictionary.NAME);
    private final NodeID<AttrDef<LocalDateTime>> birthday = new NodeID<>(Dictionary.BIRTHDAY);
    private final NodeID<AttrDef<List<String>>> accounts = new NodeID<>(Dictionary.ACCOUNTS);


    // components
    private final NodeID<AddressDef> work = new NodeID<>(1, "work", AddressDef.INSTANCE);
    private final NodeID<AddressDef> home = new NodeID<>(2, "home", AddressDef.INSTANCE);

    // groups
    private final NodeID<GroupDef<Book, BookDef>> books = new NodeID<>(new GroupDef<>(7, "books", BookDef.INSTANCE));

    PersonDef() {
        super(1, "person");
    }

    public NodeID<AttrDef<String>> name() {
        return name;
    }

    public NodeID<AttrDef<LocalDateTime>> birthday() {return birthday;}

    public NodeID<AttrDef<List<String>>> accounts() {return accounts; }

    public NodeID<AddressDef> work() {
        return work;
    }

    public NodeID<AddressDef> home() {
        return home;
    }

    public NodeID<GroupDef<Book, BookDef>> books() {
        return books;
    }

    public Person create(StateTracker tracker) {
        return create(ROOT_ID, tracker);
    }

    public Person create(NodePath<PersonDef> path, StateTracker tracker) {
        return new Person(path, tracker);
    }
}
