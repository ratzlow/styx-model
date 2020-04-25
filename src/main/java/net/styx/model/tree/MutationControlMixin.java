package net.styx.model.tree;

// TODO (FRa) : (FRa): is object creation to big overhead? design vs. perf opt
public class MutationControlMixin implements MutationControl {

    private boolean frozen = false;

    @Override
    public boolean freeze() {
        boolean switched = !frozen;
        frozen = true;
        return switched;
    }

    @Override
    public boolean unfreeze() {
        boolean switched = frozen;
        frozen = false;
        return switched;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    // TODO (FRa) : (FRa): supply better msg
    public void checkFrozen(MutationControl checkedItem) {
        if (frozen) {
            throw new UnsupportedOperationException("Item is frozen. No mutation allowed! " + checkedItem);
        }
    }
}
