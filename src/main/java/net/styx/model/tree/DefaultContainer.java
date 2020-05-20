package net.styx.model.tree;

import net.styx.model.meta.DataType;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.leaf.*;
import net.styx.model.tree.traverse.ImmutableLeaf;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;


/**
 * The addition of an attribute (node/leaf) is only considered as a change if it comes along with
 * an attribute value change. The pure add operation of a clean attribute does not mark
 * the container dirty.
 */
public class DefaultContainer implements Container {

    private static final Map<DataType, Leaf> UNSET_LEAFS = createUnsetLeafs();
    private static final Map<DataType, Function<NodeID, Leaf>> LEAF_GENERATORS = createLeafGenerators();

    private final NodeID nodeID;
    private final MapStore<Node> store;


    //------------------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------------------

    public DefaultContainer(NodeID nodeID) {
        this(nodeID, emptyList());
    }

    public DefaultContainer(NodeID nodeID, long idx) {
        this(new IdxNodeID(nodeID.getDescriptor(), idx), emptyList());
    }

    public DefaultContainer(NodeID nodeID, Collection<Node> children) {
        this.nodeID = nodeID;

        Map<NodeID, Node> allNodes = children.stream().collect(
                toMap(Node::getNodeID, Function.identity(),
                        (existing, replacement) -> existing, HashMap::new)
        );
        this.store = new MapStore<>(allNodes);
    }


    //------------------------------------------------------------------------------
    // public API
    //------------------------------------------------------------------------------


    public <T> T getLeafValue(NodeID nodeID, Function<Leaf, T> dispatchGet) {
        memberCheck(nodeID);
        final Leaf leaf;
        if (store.getLive().containsKey(nodeID)) {
            leaf = asLeaf(store.getLive().get(nodeID));
        } else {
            leaf = UNSET_LEAFS.get(nodeID.getDescriptor().getDataType());
        }

        return dispatchGet.apply(leaf);
    }

    @Override
    public Leaf getLeaf(NodeID nodeID) {
        memberCheck(nodeID);
        DataType dataType = nodeID.getDescriptor().getDataType();
        return asLeaf(store.getLive().getOrDefault(nodeID, UNSET_LEAFS.get(dataType)));
    }

    /**
     * Create leaf on the fly if none exists and apply setter to set new value on it.
     *
     * @param nodeID      of leaf
     * @param dispatchSet type matching setter
     */
    public Container setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet) {
        memberCheck(nodeID);
        store.checkBackup();
        Function<NodeID, Leaf> leafGenerator = LEAF_GENERATORS.get(nodeID.getDescriptor().getDataType());
        Node node = store.getLive().computeIfAbsent(nodeID, leafGenerator);
        dispatchSet.accept(asLeaf(node));

        return this;
    }

    @Override
    public Container setLeaf(Leaf leaf) {
        memberCheck(leaf.getNodeID());
        store.checkBackup();
        store.getLive().put(leaf.getNodeID(), leaf);

        return this;
    }


    @Override
    public Container setContainer(Container container) {
        memberCheck(container.getNodeID());
        store.checkBackup();
        store.getLive().put(container.getNodeID(), container);

        return this;
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
    public <E extends Node> Container setGroup(Group<E> group) {
        memberCheck(group.getNodeID());
        store.checkBackup();
        store.getLive().put(group.getNodeID(), group);

        return this;
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
    }

    @Override
    public Iterator<Node> children() {
        return store.getLive().values().iterator();
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
        unsetLeafs.put(DataType.BIG_DECIMAL, new BigDecimalLeaf(NodeID.UNDEF));
        unsetLeafs.put(DataType.INT, new IntLeaf(NodeID.UNDEF));
        unsetLeafs.put(DataType.LONG, new LongLeaf(NodeID.UNDEF));
        unsetLeafs.put(DataType.STRING, new StringLeaf(NodeID.UNDEF));
        unsetLeafs.put(DataType.ENUM, new EnumLeaf(NodeID.UNDEF));

        unsetLeafs.replaceAll((dataType, leaf) -> new ImmutableLeaf(leaf));
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
