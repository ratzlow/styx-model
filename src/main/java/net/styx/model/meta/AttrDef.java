package net.styx.model.meta;

public class AttrDef<E> implements NodeDef<E> {
    private final int id;
    private final String name;
    private final Class<E> clazz;

    public AttrDef(int id, String name, Class<E> clazz) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
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
