package net.styx.model.changelog;

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
}
