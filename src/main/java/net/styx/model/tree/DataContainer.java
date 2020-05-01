package net.styx.model.tree;

import net.styx.model.meta.DataType;
import net.styx.model.meta.NodeID;
import net.styx.model.meta.NodeType;
import net.styx.model.tree.leaf.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


/**
 * The addition of an attribute (node/leaf) is only considered as a change if it comes along with
 * an attribute value change. The pure add operation of a clean attribute does not mark
 * the container dirty.
 *
 * <p>
 * // TODO (FRa) : (FRa): add API to discarded empty/unset.
 */
public class DataContainer implements Container {

    // TODO (FRa) : (FRa): make sure those leafs are immutable, to avoid side effects
    private static final Map<DataType, Leaf> UNSET_LEAFS = createUnsetLeafs();
    private static final Map<DataType, Function<NodeID, Leaf>> LEAF_GENERATORS = createLeafGenerators();

    private final NodeID nodeID;
    // TODO (FRa) : (FRa): deep clones needed in current & previous to avoid side
    //  effects of manipulation items in both containers
    private final State current;
    private final State previous;

    //------------------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------------------

    public DataContainer(NodeID nodeID) {
        this(nodeID, emptyList(), emptyList(), emptyList());
    }

    public DataContainer(NodeID nodeID, long idx) {
        this(new IdxNodeID(nodeID.getDescriptor(), idx), emptyList(), emptyList(), emptyList());
    }


    public DataContainer(NodeID nodeID, Collection<Leaf> initialLeafs) {
        this(nodeID, initialLeafs, Collections.emptySet(), emptyList());
    }

    private DataContainer(NodeID nodeID,
                          Collection<Leaf> initialLeafs,
                          Collection<Container> initialContainers,
                          Collection<Group<?>> initialGroups) {
        this.nodeID = nodeID;
        this.previous = State.freeze(initialLeafs, initialContainers, initialGroups);
        this.current = State.hot(initialLeafs, initialContainers, initialGroups);
    }


    //------------------------------------------------------------------------------
    // public API
    //------------------------------------------------------------------------------

    @Deprecated
    public <T> T get(NodeID nodeID, Function<Leaf, T> dispatchGet) {
        checkRange(nodeID);
        Leaf leaf = current.leafs.getOrDefault(nodeID, UNSET_LEAFS.get(nodeID.getDescriptor().getDataType()));
        return dispatchGet.apply(leaf);
    }

    @Deprecated
    public void set(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        checkRange(nodeID);
        Function<NodeID, Leaf> leafGenerator = LEAF_GENERATORS.get(nodeID.getDescriptor().getDataType());
        Leaf leaf = current.leafs.computeIfAbsent(nodeID, leafGenerator);
        dispatchSet.accept(leaf);
    }

    @Override
    public void setContainer(Container container) {
        current.containers.put(container.getNodeID().getDescriptor(), container);
    }

    @Override
    public <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz) {
        Node node = getContainer(nodeID);
        return node != null ? clazz.cast(node) : null;
    }

    @Override
    public boolean remove(NodeID nodeID) {
        return current.remove(nodeID);
    }

    @Override
    public Container getContainer(NodeID nodeID) {
        checkRange(nodeID);
        return current.containers.get(nodeID);
    }

    @Override
    public void setLeaf(Leaf leaf) {
        checkRange(leaf.getNodeID().getDescriptor());
        current.leafs.put(leaf.getNodeID().getDescriptor(), leaf);
    }

    @Override
    public Leaf getLeaf(NodeID nodeID) {
        checkRange(nodeID);
        return current.leafs.get(nodeID);
    }

    @Override
    public <E extends Node> void setGroup(Group<E> group) {
        checkRange(group.getNodeID().getDescriptor());
        current.groups.put(group.getNodeID().getDescriptor(), group);
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID) {
        return getGroupInternal(nodeID);
    }

    @Override
    public <E extends Node> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz) {
        return getGroupInternal(nodeID);
    }

    private <E extends Node> Group<E> getGroupInternal(NodeID nodeID) {
        checkRange(nodeID);
        Group<? extends Node> group = current.groups.computeIfAbsent(nodeID, desc -> new Group<E>(nodeID));
        return (Group<E>) group;
    }

    @Override
    public NodeID getNodeID() {
        return nodeID;
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
        // revert all items to apply contract to tree
        current.allValues().forEach(Stateful::rollback);
    }

    @Override
    public void accept(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
        current.allValues().forEach(e -> e.accept(treeWalker));
        treeWalker.onExit(nodeID);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataContainer.class.getSimpleName() + "[", "]")
                .add("nodeID=" + nodeID)
                .add("current=" + current)
                .add("previous=" + previous)
                .toString();
    }

//--------------------------------------------------------------------------------------
    // internal implementation
    //--------------------------------------------------------------------------------------

    private void checkRange(NodeID nodeID) {
        if (!this.nodeID.getDescriptor().getChildren().contains(nodeID.getDescriptor())) {
            String msg = String.format("Attribute %s is not defined for %s", nodeID, this.nodeID);
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
    private static Map<DataType, Function<NodeID, Leaf>> createLeafGenerators() {
        Map<DataType, Function<NodeID, Leaf>> generators = new EnumMap<>(DataType.class);
        generators.put(DataType.BIG_DECIMAL, BigDecimalLeaf::new);
        generators.put(DataType.INT, IntLeaf::new);
        generators.put(DataType.LONG, LongLeaf::new);
        generators.put(DataType.STRING, StringLeaf::new);
        generators.put(DataType.ENUM, EnumLeaf::new);
        return generators;
    }


    private static class State {
        private final Map<NodeID, Leaf> leafs;
        private final Map<NodeID, Container> containers;
        private final Map<NodeID, Group<? extends Node>> groups;

        //---------------------------------------------------------
        // constructors & factories
        //---------------------------------------------------------

        private State(Map<NodeID, Leaf> leafs,
                      Map<NodeID, Container> containers,
                      Map<NodeID, Group<?>> groups) {
            this.leafs = leafs;
            this.containers = containers;
            this.groups = groups;
        }

        static State freeze(Collection<Leaf> leafs, Collection<Container> containers, Collection<Group<?>> groups) {
            return new State(
                    Collections.unmodifiableMap(asMap(leafs)),
                    Collections.unmodifiableMap(asMap(containers)),
                    Collections.unmodifiableMap(asMap(groups))
            );
        }

        static State hot(Collection<Leaf> leafs, Collection<Container> containers, Collection<Group<?>> groups) {
            return new State(asMap(leafs), asMap(containers), asMap(groups));
        }

        //---------------------------------------------------------
        // API
        //---------------------------------------------------------

        Stream<Node> allValues() {
            return Stream.of(
                    leafs.values().stream(),
                    containers.values().stream(),
                    groups.values().stream()
            ).flatMap(Function.identity());
        }

        boolean remove(NodeID nodeID) {
            // TODO (FRa) : (FRa): use this data structure as primary structure to avoid single maps?
            Map<NodeType, Map<NodeID, ?>> members = new EnumMap<>(NodeType.class);
            members.put(NodeType.CONTAINER, containers);
            members.put(NodeType.GROUP, groups);

            Map<NodeID, ?> attributeContainer = members.getOrDefault(nodeID.getDescriptor().getNodeType(), leafs);
            return attributeContainer.remove(nodeID) != null;
        }

        //---------------------------------------------------------
        // impl details
        //---------------------------------------------------------

        // TODO (FRa) : (FRa): perf: replace with Enum!?
        private static <T extends Node> Map<NodeID, T> asMap(Collection<T> nodes) {
            return nodes.stream().collect(
                    toMap(Node::getNodeID, identity(), (existing, replacement) -> existing, HashMap::new)
            );
        }
    }
}
