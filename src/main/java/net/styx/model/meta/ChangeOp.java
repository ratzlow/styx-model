package net.styx.model.meta;

public class ChangeOp<T> {
    public enum Type {SET, REMOVE}

    final Type type;
    final NodePath<?> path;
    final T before;
    final T after;

    public ChangeOp(Type type, NodePath<?> path, T before, T after) {
        this.type = type;
        this.path = path;
        this.before = before;
        this.after = after;
    }

    @Override
    public String toString() {
        return String.format("%s%s: %s -> %s", type, path, format(before), format(after));
    }

    private static String format(Object o) {
        return o != null ? '\'' + o.toString() + '\'' : "null";
    }
}
