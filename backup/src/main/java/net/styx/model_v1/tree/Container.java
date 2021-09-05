package net.styx.model_v1.tree;

import net.styx.model_v1.meta.NodeID;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Container extends StatefulNode {

    //------------------------------------------------------------------
    // Leaf accessors
    //------------------------------------------------------------------

    /**
     * @param leaf
     * @throws NullPointerException if leaf is null
     */
    Container setLeaf(Leaf leaf);

    Container setLeaf(NodeID nodeID, Consumer<Leaf> dispatchSet);

    Leaf getLeaf(NodeID nodeID);

    <T> T getLeafValue(NodeID nodeID, Function<Leaf, T> dispatchGet);


    //------------------------------------------------------------------
    // Node accessors
    //------------------------------------------------------------------

    /**
     * @param container
     * @throws NullPointerException if node is null
     */
    Container setContainer(Container container);

    Container getContainer(NodeID nodeID);

    <T extends Container> T getContainer(NodeID nodeID, Class<T> clazz);


    //------------------------------------------------------------------
    // Group accessors
    //------------------------------------------------------------------

    <E extends StatefulNode> Container setGroup(Group<E> group);

    /**
     * @param nodeID identifier of item to fetch or create
     * @return either existing or lazily created {@link Group} container.
     */
    <E extends StatefulNode> Group<E> getGroup(NodeID nodeID);

    <E extends StatefulNode> Group<E> getGroup(NodeID nodeID, Class<E> elementClazz);
}
