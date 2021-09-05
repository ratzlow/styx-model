package net.styx.model.sample;

import net.styx.model.changelog.NodePath;
import net.styx.model.changelog.StateTracker;
import net.styx.model.sample.meta.PersonDef;

public class Person {
    private final PersonDef def;
    private final NodePath<PersonDef> path;
    private final StateTracker tracker;

    public Person(NodePath<PersonDef> path, StateTracker stateTracker) {
        this.path = path;
        this.def = path.getLeaf().def();
        this.tracker = stateTracker;
    }
    
    public String getName() {
        return tracker.get(path, def.name());
    }

    public void setName(String name) {
        tracker.set(path, def.name(), name);
    }

    public Address getHome() {
        return tracker.get(path, def.home());
    }

    public void setHome(Address home) {
        tracker.set(path, def.home(), home);
    }

    public Address getWork() {
        return tracker.get(path, def.work());
    }

    public void setWork(Address work) {
        tracker.set(path, def.work(), work);
    }
}
