package net.styx.model.meta;

public class ComponentDef<E> implements NodeDef<E> {
    private final int id;
    private final String name;

    public ComponentDef(int id, String name) {
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
