package net.styx.model.traverse;

import net.styx.model.tree.DataContainer;
import net.styx.model.tree.Group;
import net.styx.model.tree.IdentifiableDataContainer;
import net.styx.model.tree.Leaf;

interface NodeFactory {
    DataContainer createDataContainer();
    IdentifiableDataContainer createIdentifiableDataContainer();
    Leaf createLeaf();
}
