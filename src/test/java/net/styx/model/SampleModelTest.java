package net.styx.model;

import net.styx.model.meta.*;
import net.styx.model.sample.Address;
import net.styx.model.sample.Book;
import net.styx.model.sample.Person;
import net.styx.model.sample.meta.AddressType;
import net.styx.model.sample.meta.BookType;
import net.styx.model.sample.meta.PersonType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

// TODO: test cannot add some node twice to collection! really? Check return val
// todo: test add/rm of Component with sub tree
public class SampleModelTest {

    /**
     * Test cases:
     * - What you set is what you get!
     */
    @Test
    void wirePojoStyle() {
        final StateTracker tracker = new StateTracker();
        PersonType personDef = PersonType.INSTANCE;
        Person person = personDef.create(tracker);

        person.setName("Frank");
        Assertions.assertEquals("Frank", person.getName());

        // trace changes on nested structures
        Assertions.assertNull(person.getHome());

        NodePath<AddressType> homePath = new NodePath<>(PersonType.ROOT_ID, personDef.home());
        Address home = AddressType.INSTANCE.create(homePath, tracker);
        home.setStreet("Mainstreet");
        person.setHome(home);
        Assertions.assertEquals("Mainstreet", person.getHome().getStreet());
        Assertions.assertNull(person.getWork());
        Assertions.assertSame(home, person.getHome());

        String newStreet = "NewStreet";
        person.getHome().setStreet(newStreet);
        Assertions.assertSame(newStreet, person.getHome().getStreet());

        person.setHome(null);
        Assertions.assertThrows(NullPointerException.class, () -> person.getHome().setStreet("InvalidStreet"));
    }

    @Test
    void nullSafeGetOnContainer() {
        Person person = createPerson();
        person.setName("Laeti");

        LocalDateTime birthday = LocalDateTime.now();
        person.setBirthday(birthday);
        Assertions.assertSame(birthday, person.getBirthday());

        String street = "CarnivalStreet";
        person.home().setStreet(street);
        Assertions.assertSame(street, person.getHome().getStreet());
        Assertions.assertSame(street, person.home().getStreet());

        person.getHome().setZip(123);
        Assertions.assertEquals(person.getHome().getZip(), person.home().getZip());

        person.work().setStreet("MainStreet");
        person.work().setZip(456);
        Assertions.assertNotSame(person.work(), person.home());
        Assertions.assertNotEquals(person.work(), person.home());

        System.out.println("Done");
    }

    @Test
    void addDisconnectedComponent() {
        // first check the base case of disconnected item
        StateTracker tracker = new StateTracker();
        Book book = new Book(tracker);
        String description = "Exciting";
        book.setDescription(description);

        NavigableMap<NodePath<?>, Object> nodes = tracker.getNodes();
        Assertions.assertEquals(1, nodes.size());
        Map.Entry<NodePath<?>, Object> entry = nodes.entrySet().stream().findFirst().orElseThrow();

        NodePath<?> storeKey = entry.getKey();
        Object value = entry.getValue();

        Assertions.assertEquals(2, storeKey.getNodeIDs().size());
        Assertions.assertEquals(description, value);

        // now add it to a collection
        StateTracker booksTracker = new StateTracker();
        GroupType<Book, Collection<Book>, BookType> booksDef = new GroupType<>(7, "books", BookType.INSTANCE);
        NodePath<GroupType<Book, Collection<Book>, BookType>> booksPath = new NodePath<>(new NodeID<>(booksDef));
        Group<Book, BookType> books = new Group<>(booksPath, booksTracker);
        Assertions.assertEquals(0, booksTracker.getNodes().size());
        Assertions.assertEquals(0, books.size());

        // now merge Book into graph with group as its now head
        Assertions.assertTrue(books.add(book));
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(2, booksTracker.getNodes().size());

        Book anotherBook = new Book();
        anotherBook.setDescription("Boring");
        books.add(anotherBook);
        Assertions.assertEquals(2, books.size());
        Assertions.assertEquals(4, booksTracker.getNodes().size());

        Assertions.assertTrue(books.contains(book));
        Assertions.assertTrue(books.remove(book));
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(2, booksTracker.getNodes().size());

        // check we have a new instance
        Assertions.assertNotSame(2, tracker.getNodes().size());
    }

    @Test
    void attributeAsList() {
        StateTracker tracker = new StateTracker();
        Person p = new Person(PersonType.ROOT_ID, tracker);
        Assertions.assertNull(p.getAccounts());
        List<String> accounts = new ArrayList<>(List.of("123", "456"));
        p.setAccounts(accounts);
        Assertions.assertEquals(accounts, p.getAccounts());
        Assertions.assertThrows(Exception.class, () -> p.getAccounts().add("789"));
    }

    @Test
    void nullSafeGetOnGroup() {
        Person person = createPerson();
        // check collection exists
        Assertions.assertNull(person.getBooks());
        Collection<Book> books = person.books();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(books, person.getBooks());
        person.setBooks(null);
        Assertions.assertNull(person.getBooks());

        // re-init collection
        Collection<Book> otherBooks = person.books();
        Assertions.assertNotEquals(books, otherBooks);

        // add elements
        Book book_1 = new Book();
        person.books().add(book_1);
    }

    private Person createPerson() {
        StateTracker tracker = new StateTracker();
        PersonType personDef = PersonType.INSTANCE;
        return personDef.create(tracker);
    }
}
