package net.styx.model.sample;

import net.styx.model.meta.Group;
import net.styx.model.meta.NodePath;
import net.styx.model.meta.StateTracker;
import net.styx.model.sample.meta.AddressDef;
import net.styx.model.sample.meta.PersonDef;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Person {
    private static final PersonDef DEF = PersonDef.INSTANCE;
    private final NodePath<PersonDef> path;
    private final StateTracker tracker;

    public Person(NodePath<PersonDef> path, StateTracker stateTracker) {
        this.path = path;
        this.tracker = stateTracker;
    }
    
    public String getName() {
        return tracker.get(path, DEF.name());
    }

    public void setName(String name) {
        tracker.set(path, DEF.name(), name);
    }

    public void setAccounts(List<String> accounts) {
        tracker.set(path, DEF.accounts(), accounts);
    }

    public List<String> getAccounts() {
        return tracker.get(path, DEF.accounts());
    }

    public LocalDateTime getBirthday() {
        return tracker.get(path, DEF.birthday());
    }

    public void setBirthday(LocalDateTime birthday) {
        tracker.set(path, DEF.birthday(), birthday);
    }

    public Address getHome() {
        return tracker.get(path, DEF.home());
    }

    public Address home() {
        return tracker.get(path, DEF.home(), fqPath -> AddressDef.INSTANCE.create(fqPath, tracker));
    }

    public void setHome(Address home) {
        tracker.set(path, DEF.home(), home);
    }

    public Address getWork() {
        return tracker.get(path, DEF.work());
    }

    public Address work() {
        return tracker.get(path, DEF.work(), fqPath -> AddressDef.INSTANCE.create(fqPath, tracker));
    }

    public void setWork(Address work) {
        tracker.set(path, DEF.work(), work);
    }

    public Collection<Book> getBooks() {
        return tracker.get(path, DEF.books());
    }

    public Collection<Book> books() {
        return tracker.get(path, DEF.books(), fqPath -> new Group<>(fqPath, tracker));
    }

    public void setBooks(Collection<Book> books) {
        tracker.set(path, DEF.books(), books);
    }

    @Override
    public String toString() {
        return "Person{path='" + path + '}';
    }
}
