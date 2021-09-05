package net.styx.model_v1.meta;

import net.styx.model_v1.tree.Container;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public interface NodeID {

    NodeID UNDEF = UndefDescriptor::new;

    long NO_IDX = 0;

    Descriptor getDescriptor();

    default long getIdx() {
        return NO_IDX;
    }

    //--------------------------------------------------------------------------------------
    // inner classes
    //--------------------------------------------------------------------------------------

    /**
     * Singleton as place holder if no valid business attribute reflecting
     * Descriptor is known. Will not relate to the domain.
     */
    final class UndefDescriptor implements Descriptor {
        @Override public int getTagNumber() { return 0; }

        @Override public String shortName() { return "UNDEF"; }

        @Override public String alias() { return "UNDEF"; }

        @Override public NodeType getNodeType() { return NodeType.LEAF; }

        @Override public DataType getDataType() { return DataType.INT; }

        @Override public Set<Descriptor> getChildren() { return Collections.emptySet(); }

        @Override public Function<Container, Container> getDomainModelFactory() { return Function.identity(); }
    }
}
