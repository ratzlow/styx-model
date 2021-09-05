package net.styx.model_v1.meta;

import net.styx.model_v1.tree.Container;

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
