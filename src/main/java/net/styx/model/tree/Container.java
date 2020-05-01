package net.styx.model.tree;

import net.styx.model.meta.NodeID;

// TODO (FRa) : (FRa): simplify interface to pass NodeType to unify across different subclasses;
// TODO (FRa) : (FRa): rewrite setters to allow method chaining
public interface Container extends Node {

    //------------------------------------------------------------------
    // Leaf accessors
    //------------------------------------------------------------------

    /**
     * @param leaf
     * @throws NullPointerException if leaf is null
     */
    void setLeaf(Leaf leaf);

    Leaf getLeaf(NodeID nodeID);


    //------------------------------------------------------------------
    // Node accessors
    //------------------------------------------------------------------

    /**
     * @param container
     * @throws NullPointerException if node is null
     */
    void setContainer(Container container);

    Container getContainer(NodeID nodeID);

    <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz);


    //------------------------------------------------------------------
    // Group accessors
    //------------------------------------------------------------------

    <E extends Node> void setGroup(Group<E> group);

    /**
     * @param nodeID identifier of item to fetch or create
     * @return either existing or lazily created {@link Group} container.
     */
    <E extends Node> Group<E> getGroup(NodeID nodeID);

    <E extends Node> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz);


    //------------------------------------------------------------------
    // common operations
    //------------------------------------------------------------------

    /**
     * Remove attribute identified by #descriptor. Similar to setting an attribute to null.
     * Makes most sense to establish on delegating POJOs the set-to-null contract.
     *
     * @param nodeID in dictionary that identifies this node
     * @return true ... if existing attribute was deleted and changed the state of the node,
     * false ... if it was not part of the Node and not state change triggered
     */
    boolean remove(NodeID nodeID);
}
