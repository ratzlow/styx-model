package net.styx.model.meta;

import java.util.Collection;

public class GroupDef<E, C extends Collection<E>, T extends NodeDef<E>> implements NodeDef<C> {

    private final int id;
    private final String name;
    private final T elementDef;

    public GroupDef(int id, String name, T elementDef) {
        this.id = id;
        this.name = name;
        this.elementDef = elementDef;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getDefaultName() {
        return name;
    }

    public T getElementDef() {
        return elementDef;
    }
}