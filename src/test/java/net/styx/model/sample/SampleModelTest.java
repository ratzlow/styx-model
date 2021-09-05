package net.styx.model.sample;

import net.styx.model.changelog.NodePath;
import net.styx.model.changelog.StateTracker;
import net.styx.model.sample.meta.AddressDef;
import net.styx.model.sample.meta.PersonDef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SampleModelTest {

    @Test
    void wirePojoStyle() {
        final StateTracker tracker = new StateTracker();
        PersonDef personDef = PersonDef.INSTANCE;
        Person person = personDef.create(tracker);

        person.setName("Frank");
        Assertions.assertEquals("Frank", person.getName());

        // trace changes on nested structures
        Assertions.assertNull(person.getHome());
        NodePath<AddressDef> homePath = new NodePath<>(PersonDef.ROOT_ID, personDef.home());
        Address home = AddressDef.INSTANCE.create(homePath, tracker);
        home.setStreet("Mainstreet");
        person.setHome(home);
        Assertions.assertEquals("Mainstreet", person.getHome().getStreet());

        person.getHome().setStreet("NewStreet");
        Assertions.assertEquals("NewStreet", person.getHome().getStreet());

        person.setHome(null);
        Assertions.assertNull(person.getHome());
    }
}
