package net.styx.model.tree;

import net.styx.model.meta.DataType;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.leaf.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;


/**
 * The addition of an attribute (node/leaf) is only considered as a change if it comes along with
 * an attribute value change. The pure add operation of a clean attribute does not mark
 * the container dirty.
 *
 * <p>
 * // TODO (FRa) : (FRa): add API to discarded empty/unset.
 */
public class DefaultContainer implements Container {

    // TODO (FRa) : (FRa): make sure those leafs are immutable, to avoid side effects
    private static final Map<DataType, Leaf> UNSET_LEAFS = createUnsetLeafs();
    private static final Map<DataType, Function<NodeID, Leaf>> LEAF_GENERATORS = createLeafGenerators();

    private final NodeID nodeID;
    private final MapStore<Node> store;


    //------------------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------------------

    public DefaultContainer(NodeID nodeID) {
        this(nodeID, emptyList(), emptyList(), emptyList());
    }

    public DefaultContainer(NodeID nodeID, long idx) {
        this(new IdxNodeID(nodeID.getDescriptor(), idx), emptyList(), emptyList(), emptyList());
    }

    public DefaultContainer(NodeID nodeID, Collection<Leaf> initialLeafs) {
        this(nodeID, initialLeafs, Collections.emptySet(), emptyList());
    }

    private DefaultContainer(NodeID nodeID,
                             Collection<Leaf> initialLeafs,
                             Collection<Container> initialContainers,
                             Collection<Group<?>> initialGroups) {
        this.nodeID = nodeID;


        Map<NodeID, Node> allNodes = Stream.of(initialLeafs, initialContainers, initialGroups)
                .flatMap(Collection::stream)
                .collect(toMap(Node::getNodeID, Function.identity(),
                        (existing, replacement) -> existing, HashMap::new));
        this.store = new MapStore<>(allNodes);
    }


    //------------------------------------------------------------------------------
    // public API
    //------------------------------------------------------------------------------


    @Deprecated
    public <T> T get(NodeID nodeID, Function<Leaf, T> dispatchGet) {
        memberCheck(nodeID);
        final Leaf leaf;
        if (store.getLive().containsKey(nodeID)) {
            leaf = asLeaf(store.getLive().get(nodeID));
        } else {
            // TODO (FRa) : (FRa): return immutable default value leaf
            leaf = UNSET_LEAFS.get(nodeID.getDescriptor().getDataType());
        }

        return dispatchGet.apply(leaf);
    }

    /**
     * Create leaf on the fly if none exists and apply setter to set new value on it.
     *
     * @param nodeID      of leaf
     * @param dispatchSet type matching setter
     */
    public void setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        memberCheck(nodeID);
        store.checkBackup();
        Function<NodeID, Leaf> leafGenerator = LEAF_GENERATORS.get(nodeID.getDescriptor().getDataType());
        Node node = store.getLive().computeIfAbsent(nodeID, leafGenerator);
        dispatchSet.accept(asLeaf(node));
    }

    @Override
    public void setLeaf(Leaf leaf) {
        memberCheck(leaf.getNodeID());
        store.checkBackup();
        store.getLive().put(leaf.getNodeID(), leaf);
    }

    @Override
    public Leaf getLeaf(NodeID nodeID) {
        memberCheck(nodeID);
        DataType dataType = nodeID.getDescriptor().getDataType();
        return asLeaf(store.getLive().getOrDefault(nodeID, UNSET_LEAFS.get(dataType)));
    }

    @Override
    public void setContainer(Container container) {
        memberCheck(container.getNodeID());
        store.checkBackup();
        store.getLive().put(container.getNodeID(), container);
    }

    @Override
    public <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz) {
        memberCheck(nodeID);
        Node node = getContainer(nodeID);
        return node != null ? clazz.cast(node) : null;
    }

    @Override
    public Container getContainer(NodeID nodeID) {
        memberCheck(nodeID);
        Node node = store.getLive().get(nodeID);
        return asContainer(node);
    }

    @Override
    public <E extends Node> void setGroup(Group<E> group) {
        memberCheck(group.getNodeID());
        store.checkBackup();
        store.getLive().put(group.getNodeID(), group);
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
        memberCheck(nodeID);
        Node group = store.getLive().get(nodeID);
        return asGroup(group);
    }

    @Override
    public boolean remove(NodeID nodeID) {
        return store.remove(nodeID);
    }

    @Override
    public NodeID getNodeID() {
        return nodeID;
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean isChanged() {
        return store.isChanged();
    }

    @Override
    public void commit() {
        store.commit();
    }

    @Override
    public void rollback() {
        store.rollback();
    }

    @Override
    public void accept(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
        // TODO (FRa) : (FRa): centralize the iteration?
        Collection<Node> nodes = store.getLive().values();
        for (Iterator<Node> iter = nodes.iterator(); iter.hasNext() && treeWalker.proceed(); ) {
            iter.next().accept(treeWalker);
        }
        treeWalker.onExit(nodeID);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultContainer.class.getSimpleName() + "[", "]")
                .add("nodeID=" + nodeID)
                .add("store.live=" + store.getLive())
                .toString();
    }

    //--------------------------------------------------------------------------------------
    // internal implementation
    //--------------------------------------------------------------------------------------

    private void memberCheck(NodeID nodeID) {
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

    private Leaf asLeaf(Node node) {
        return node != null ? (Leaf) node : null;
    }

    private Container asContainer(Node node) {
        return node != null ? (Container) node : null;
    }

    private <E extends Node> Group<E> asGroup(Node node) {
        return node != null ? Group.class.cast(node) : null;
    }
}
