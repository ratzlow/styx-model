package net.styx.model.meta;

public class ComponentType<E> implements NodeType<E> {
    private final int id;
    private final String name;

    public ComponentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getDefaultName() {
        return name;
    }
}
