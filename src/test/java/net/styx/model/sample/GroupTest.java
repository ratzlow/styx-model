package net.styx.model.sample;

import net.styx.model.meta.ComponentType;
import net.styx.model.meta.GroupType;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class GroupTest {

    ComponentType<String> compDefA = new ComponentType<>(1, "Component_A");

    GroupType<String, Collection<String>, ComponentType<String>> groupDefB = new GroupType<>(5, "Group_B", compDefA);

    GroupType<Collection<String>,
                Collection<Collection<String>>,
            GroupType<String, Collection<String>, ComponentType<String>>> groupDefC = new GroupType<>(10, "Group_C", groupDefB);

    @Test
    void testFirstLevelOperation() {
        class CompA {}
        ComponentType<CompA> componentDefA = new ComponentType<>(20, "A_Component");
    }
}
