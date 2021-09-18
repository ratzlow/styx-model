package net.styx.model.meta;

import java.util.Collection;

public class GroupType<E, C extends Collection<E>, T extends NodeType<E>> implements NodeType<C> {

    private final int id;
    private final String name;
    private final T elementType;

    public GroupType(int id, String name, T elementType) {
        this.id = id;
        this.name = name;
        this.elementType = elementType;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getDefaultName() {
        return name;
    }

    public T getElementType() {
        return elementType;
    }
}