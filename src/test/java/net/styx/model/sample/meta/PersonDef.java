package net.styx.model.sample.meta;

import net.styx.model.changelog.NodePath;
import net.styx.model.changelog.StateTracker;
import net.styx.model.meta.AttrDef;
import net.styx.model.meta.ComponentDef;
import net.styx.model.meta.NodeID;
import net.styx.model.sample.Person;

public class PersonDef extends ComponentDef<Person> {
    public static final PersonDef INSTANCE = new PersonDef();
    public static final NodePath<PersonDef> ROOT_ID = new NodePath<>(new NodeID<>(INSTANCE));

    // attributes
    private final NodeID<AttrDef<String>> name = new NodeID<>(Dictionary.NAME);

    // components
    private final NodeID<AddressDef> work = new NodeID<>(1, "work", AddressDef.INSTANCE);
    private final NodeID<AddressDef> home = new NodeID<>(2, "home", AddressDef.INSTANCE);

    PersonDef() {
        super(1, "person");
    }

    public NodeID<AttrDef<String>> name() {
        return name;
    }

    public NodeID<AddressDef> work() {
        return work;
    }

    public NodeID<AddressDef> home() {
        return home;
    }

    public Person create(StateTracker tracker) {
        return create(ROOT_ID, tracker);
    }

    public Person create(NodePath<PersonDef> path, StateTracker tracker) {
        return new Person(path, tracker);
    }
}
