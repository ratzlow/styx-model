package net.styx.model;

import net.styx.model.meta.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;

/**
 * Define nested structure of Component -> Collection<Node> -> Collection<Node>
 * Student 1 --> n Course 1 --> n Exams
 * todo: test group of groups of components
 */
public class GroupTest {

    ComponentType<String> compDefA = new ComponentType<>(1, "Component_A");

    GroupType<String, Collection<String>, ComponentType<String>> groupDefB = new GroupType<>(5, "Group_B", compDefA);

    GroupType<Collection<String>, Collection<Collection<String>>, GroupType<String, Collection<String>, ComponentType<String>>> groupDefC = new GroupType<>(10, "Group_C", groupDefB);

    @Test
    void testFirstLevelOperation() {
        Student student = new Student();
    }

    //------------------------------------------- test model -----------------------------------------------------------

    private static class Student implements Node<Student.Type> {
        private final GenericNode<Student.Type> mixin;

        public Student() {
            this(new StateTracker(), Student.Type.DEFAULT_PATH);
        }

        public Student(StateTracker stateTracker, NodePath<Student.Type> path) {
            this.mixin = new GenericNode<>(stateTracker, path);
        }

        @Override
        public void connect(NodePath<Student.Type> prefix, StateTracker stateTracker) {
            mixin.connect(prefix, stateTracker);
        }

        @Override
        public void disconnect() {
            mixin.disconnect();
        }

        @Override
        public NodePath<Type> getNodePath() {
            return mixin.getNodePath();
        }

        static class Type extends ComponentType<Student> {
            public static final Type INSTANCE = new Type(13, "student");
            public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));
            // groups
            private final NodeID<GroupType<Course, Collection<Course>, Course.Type>> courses =
                    new NodeID<>(new GroupType<>(7, "books", Course.Type.INSTANCE));

            public Type(int id, String name) {
                super(id, name);
            }
        }
    }


    private static class Course implements Node<Course.Type> {
        private final GenericNode<Course.Type> mixin;

        public Course() {
            this(new StateTracker(), Course.Type.DEFAULT_PATH);
        }

        public Course(StateTracker stateTracker, NodePath<Course.Type> path) {
            this.mixin = new GenericNode<>(stateTracker, path);
        }

        @Override
        public void connect(NodePath<Course.Type> prefix, StateTracker stateTracker) {
            mixin.connect(prefix, stateTracker);
        }

        @Override
        public void disconnect() {
            mixin.disconnect();
        }

        @Override
        public NodePath<Type> getNodePath() {
            return mixin.getNodePath();
        }

        static class Type extends ComponentType<Course> {
            public static final Type INSTANCE = new Type(17, "course");
            public static final NodePath<Type> DEFAULT_PATH = new NodePath<>(new NodeID<>(INSTANCE));

            public Type(int id, String name) {
                super(id, name);
            }
        }
    }



}
