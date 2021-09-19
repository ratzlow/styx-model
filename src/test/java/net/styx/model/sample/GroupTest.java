package net.styx.model.sample;

import net.styx.model.meta.*;
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



        ComponentType<CompA> componentDefA = new ComponentType<>(20, "A_Component");
    }

    static class CompA implements Node<CompA.CompAType> {

        @Override
        public void connect(NodePath<CompAType> prefix, StateTracker stateTracker) {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public NodePath<CompAType> getNodePath() {
            return null;
        }

        static class CompAType extends ComponentType<CompA> {
            public CompAType(int id, String name) {
                super(id, name);
            }
        }
    }
}
