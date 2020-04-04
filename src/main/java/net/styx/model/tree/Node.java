package net.styx.model.tree;

import net.styx.model.meta.Descriptor;

public interface Node extends Described, Stateful {

    //------------------------------------------------------------------
    // Leaf accessors
    //------------------------------------------------------------------

    /**
     * @param leaf
     * @throws NullPointerException if leaf is null
     */
    void setLeaf(Leaf leaf);

    Leaf getLeaf(Descriptor descriptor);

    <T extends Leaf> T getLeaf(Descriptor descriptor, Class<T> clazz);


    //------------------------------------------------------------------
    // Node accessors
    //------------------------------------------------------------------

    /**
     * @param node
     * @throws NullPointerException if node is null
     */
    void setNode(Node node);

    Node getNode(Descriptor descriptor);

    <T extends Node> T getNode(Descriptor descriptor, Class<T> clazz);


    //------------------------------------------------------------------
    // Group accessors
    //------------------------------------------------------------------

    <K, E extends Node & Identifiable<K>> void setGroup(Group<K, E> group);

    Group<?, ?> getGroup(Descriptor descriptor);

    <K, E extends Node & Identifiable<K>> Group<K, E> getGroup(Descriptor descriptor,
                                                               Class<K> keyClazz,
                                                               Class<E> elementClazz);


    //------------------------------------------------------------------
    // common operations
    //------------------------------------------------------------------

    /**
     * Remove attribute identified by #descriptor. Similar to setting an attribute to null.
     * Makes most sense to establish on delegating POJOs the set-to-null contract.
     *
     * @param descriptor in dictionary that identifies this node
     * @return true ... if existing attribute was deleted and changed the state of the node,
     *         false ... if it was not part of the Node and not state change triggered
     */
    boolean remove(Descriptor descriptor);
}
