package net.styx.model.tree;

public class Diff<T> {
    enum DeltaOp {
        CREATE,
        UPDATE,
        DELETE
    }
    
    final T changed;
    final DeltaOp deltaOp;

    public Diff(T changed, DeltaOp deltaOp) {
        this.changed = changed;
        this.deltaOp = deltaOp;
    }
}
