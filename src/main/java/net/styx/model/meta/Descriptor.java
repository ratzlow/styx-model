package net.styx.model.meta;

import net.styx.model.tree.Container;

import java.util.Set;
import java.util.function.Function;

public interface Descriptor {

    int getTagNumber();

    String shortName();

    String alias();

    NodeType getNodeType();

    DataType getDataType();

    Set<Descriptor> getChildren();

    Function<Container, Container> getDomainModelFactory();
}
