package net.styx.model.tree;

public interface Identifiable<T> {

    // TODO (FRa) : (FRa): check how to make Leafs implement IF, since they dont have stable key
    T getID();
}
