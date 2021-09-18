package net.styx.model.sample;

import net.styx.model.meta.ComponentDef;
import net.styx.model.meta.GroupDef;

import java.util.Collection;

public class GroupTest {

    ComponentDef<String> compDefA = new ComponentDef<>(1, "Component_A");

    GroupDef<String, Collection<String>, ComponentDef<String>> groupDefB = new GroupDef<>(5, "Group_B", compDefA);

    GroupDef<Collection<String>,
            Collection<Collection<String>>,
            GroupDef<String, Collection<String>, ComponentDef<String>>> groupDefC = new GroupDef<>(10, "Group_C", groupDefB);
}
