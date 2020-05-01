package net.styx.model.meta;

public interface NodeID {
    long NO_IDX = 0;

    Descriptor getDescriptor();

    default long getIdx() {
        return NO_IDX;
    }
}
