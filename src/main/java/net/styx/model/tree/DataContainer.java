package net.styx.model.tree;

import net.styx.model.meta.DataType;
import net.styx.model.meta.Descriptor;
import net.styx.model.tree.leaf.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


/**
 * The addition of an attribute (node/leaf) is only considered as a change if it comes along with
 * an attribute value change. The pure add operation of a clean attribute does not mark the container dirty.
 * <p>
 * // TODO (FRa) : (FRa): On commit() all empty/unset attributes are discarded, so only value holding attributes are preserved.
 */
public class DataContainer implements Node {

    // TODO (FRa) : (FRa): make sure those leafs are immutable, to avoid side effects
    private static final Map<DataType, Leaf> UNSET_LEAFS = createUnsetLeafs();
    private static final Map<DataType, Function<Descriptor, Leaf>> LEAF_GENERATORS = createLeafGenerators();

    private final Descriptor descriptor;
    private final State current;
    private final State previous;

    //------------------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------------------

    public DataContainer(Descriptor descriptor) {
        this(descriptor, Collections.emptyList(), Collections.emptyList());
    }

    public DataContainer(Descriptor descriptor, Collection<Leaf> initialLeafs) {
        this(descriptor, initialLeafs, Collections.emptySet());
    }

    public DataContainer(Descriptor descriptor, Collection<Leaf> initialLeafs, Collection<Node> initialNodes) {
        this(descriptor, initialLeafs, initialNodes, Collections.emptySet());
    }

    public DataContainer(Descriptor descriptor, Collection<Leaf> initialLeafs, Collection<Node> initialNodes,
                         Collection<Group<?, ?>> initialGroups) {

        Map<Descriptor, Leaf> leafs = asMap(initialLeafs);
        Map<Descriptor, Node> nodes = asMap(initialNodes);
        Map<Descriptor, Group<?, ?>> groups = asMap(initialGroups);

        this.descriptor = descriptor;
        this.previous = State.freeze(leafs, nodes, groups);
        this.current = State.hot(leafs, nodes, groups);
    }

    //------------------------------------------------------------------------------
    // public API
    //------------------------------------------------------------------------------

    @Deprecated
    public <T> T get(Descriptor descriptor, Function<Leaf, T> dispatchGet) {
        checkRange(descriptor);
        Leaf leaf = current.leafs.getOrDefault(descriptor, UNSET_LEAFS.get(descriptor.getDataType()));
        return dispatchGet.apply(leaf);
    }

    @Deprecated
    public void set(Descriptor descriptor, Consumer<Leaf> dispatchSet) {
        checkRange(descriptor);
        Function<Descriptor, Leaf> leafGenerator = LEAF_GENERATORS.get(descriptor.getDataType());
        Leaf leaf = current.leafs.computeIfAbsent(descriptor, leafGenerator);
        dispatchSet.accept(leaf);
    }

    @Override
    public void setNode(Node node) {
        current.nodes.put(node.getDescriptor(), node);
    }

    @Override
    public <T extends Node> T getNode(Descriptor descriptor, Class<T> clazz) {
        Node node = getNode(descriptor);
        return node != null ? clazz.cast(node) : null;
    }

    @Override
    public boolean remove(Descriptor descriptor) {
        return current.remove(descriptor);
    }

    @Override
    public Node getNode(Descriptor descriptor) {
        checkRange(descriptor);
        return current.nodes.get(descriptor);
    }

    @Override
    public void setLeaf(Leaf leaf) {
        checkRange(leaf.getDescriptor());
        current.leafs.put(leaf.getDescriptor(), leaf);
    }

    @Override
    public Leaf getLeaf(Descriptor descriptor) {
        checkRange(descriptor);
        return current.leafs.get(descriptor);
    }

    @Override
    public <T extends Leaf> T getLeaf(Descriptor descriptor, Class<T> clazz) {
        Leaf leaf = getLeaf(descriptor);
        return leaf != null ? clazz.cast(leaf) : null;
    }

    @Override
    public <K, E extends Node & Identifiable<K>> void setGroup(Group<K, E> group) {
        checkRange(group.getDescriptor());
        current.groups.put(group.getDescriptor(), group);
    }

    @Override
    public Group<?, ?> getGroup(Descriptor descriptor) {
        checkRange(descriptor);
        return current.groups.computeIfAbsent(descriptor, desc -> new Group<>(descriptor));
    }

    @Override
    public <K, E extends Node & Identifiable<K>> Group<K, E> getGroup(Descriptor descriptor,
                                                                      Class<K> keyClazz,
                                                                      Class<E> elementClazz) {
        checkRange(descriptor);
        Group<?, ? extends Stateful> group =
                current.groups.computeIfAbsent(descriptor, desc -> new Group<K, E>(descriptor));

        // TODO (FRa) : (FRa): better way to type it?
        return (Group<K, E>) group;
    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean isEmpty() {
        return current.allValues().allMatch(Stateful::isEmpty);
    }

    @Override
    public boolean isChanged() {
        return current.allValues().anyMatch(Stateful::isChanged);
    }

    @Override
    public void commit() {
        current.allValues().forEach(Stateful::commit);
    }

    @Override
    public void rollback() {
        current.allValues().forEach(Stateful::rollback);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataContainer.class.getSimpleName() + "[", "]")
                .add("descriptor=" + descriptor)
                .add("current=" + current)
                .add("previous=" + previous)
                .toString();
    }

//--------------------------------------------------------------------------------------
    // internal implementation
    //--------------------------------------------------------------------------------------

    private void checkRange(Descriptor descriptor) {
        if (!this.descriptor.getChildren().contains(descriptor)) {
            String msg = String.format("Attribute %s is not defined for %s", descriptor, this.descriptor.toString());
            throw new IllegalArgumentException(msg);
        }
    }

    // TODO (FRa) : (FRa): inject lookup map
    private static Map<DataType, Leaf> createUnsetLeafs() {
        Map<DataType, Leaf> unsetLeafs = new EnumMap<>(DataType.class);
        unsetLeafs.put(DataType.BIG_DECIMAL, BigDecimalLeaf.EMPTY_VAL);
        unsetLeafs.put(DataType.INT, IntLeaf.EMPTY_VAL);
        unsetLeafs.put(DataType.LONG, LongLeaf.EMPTY_VAL);
        unsetLeafs.put(DataType.STRING, StringLeaf.EMPTY_VAL);
        unsetLeafs.put(DataType.ENUM, EnumLeaf.EMPTY_VAL);
        return unsetLeafs;
    }

    // TODO (FRa) : (FRa): inject lookup map
    private static Map<DataType, Function<Descriptor, Leaf>> createLeafGenerators() {
        Map<DataType, Function<Descriptor, Leaf>> generators = new EnumMap<>(DataType.class);
        generators.put(DataType.BIG_DECIMAL, BigDecimalLeaf::new);
        generators.put(DataType.INT, IntLeaf::new);
        generators.put(DataType.LONG, LongLeaf::new);
        generators.put(DataType.STRING, StringLeaf::new);
        generators.put(DataType.ENUM, EnumLeaf::new);
        return generators;
    }

    private <T extends Described> Map<Descriptor, T> asMap(Collection<T> initialLeafs) {
        return initialLeafs.stream().collect(toMap(Described::getDescriptor, identity(),
                (existing, replacement) -> existing,
                () -> new EnumMap<>(Descriptor.class))
        );
    }


    private static class State {
        private final Map<Descriptor, Leaf> leafs;
        private final Map<Descriptor, Node> nodes;
        private final Map<Descriptor, Group<?, ? extends Stateful>> groups;

        private State(Map<Descriptor, Leaf> leafs,
                      Map<Descriptor, Node> nodes,
                      Map<Descriptor, Group<?, ?>> groups) {
            this.leafs = leafs;
            this.nodes = nodes;
            this.groups = groups;
        }

        static State freeze(Map<Descriptor, Leaf> leafs,
                            Map<Descriptor, Node> nodes,
                            Map<Descriptor, Group<?, ?>> groups) {
            return new State(
                    Collections.unmodifiableMap(leafs),
                    Collections.unmodifiableMap(nodes),
                    Collections.unmodifiableMap(groups)
            );
        }

        static State hot(Map<Descriptor, Leaf> leafs,
                         Map<Descriptor, Node> nodes,
                         Map<Descriptor, Group<?, ?>> groups) {
            return new State(leafs, nodes, groups);
        }

        Stream<Stateful> allValues() {
            return Stream.of(
                    leafs.values().stream(),
                    nodes.values().stream(),
                    groups.values().stream()
            ).flatMap(Function.identity());
        }

        boolean remove(Descriptor descriptor) {
            // TODO (FRa) : (FRa): use this data structure as primary structure to avoid single maps?
            Map<DataType, Map<Descriptor, ?>> members = new EnumMap<>(DataType.class);
            members.put(DataType.COMPONENT, nodes);
            members.put(DataType.GROUP, groups);

            Map<Descriptor, ?> attributeContainer = members.getOrDefault(descriptor.getDataType(), leafs);
            return attributeContainer.remove(descriptor) != null;
        }
    }
}
